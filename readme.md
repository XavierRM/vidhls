## Video streaming api - vidhls

> &#8505; **Annotation**
>
> Following the [RFC 8216](https://datatracker.ietf.org/doc/html/rfc8216) specification for the **HLS** protocol.

We have a client (streamer) that ingests or uploads the streaming contents in a given encoded format, for example, H.264
or H.265, via HTTP, using protocols like RTMP or HLS, let's focus on the latest.

When our users client requests to view a live stream, we'll take the content
and segment it, each HTTP client request will correspond with a chunk of the video/audio
for that stream.

By itself the HLS server is not enough, depending on traffic and load experienced by our server,
it could potentially greatly degrade the quality and performance of said server, to avoid this we can add a CDN
(Content Delivery Network) server in charge of caching and storing video
segments and deliver them to end-users based on their geographic proximity.
When a viewer clicks the play button, their device sends a request to the nearest CDN server. If the requested
video segment is not already stored on the CDN server, CDN redirects the request to the origin server, where the
original
video segments reside. The CDN server delivers the requested file to the viewer and stores a local copy for future
requests,
resulting in faster content delivery.

For the ingest of video and audio data, this is, the client performing the live stream, will communicate with the server
via RTMP, so we should've another server in charge of taking and processing the video/audio data for the HLS format so
that
the HTTP server facing the users client can access it.
> <span style="color:#F5DD67">&#9888; **NOTE**</span>
>
> However, this seems to be changing with the introduction of WebRTC in OBS, making it possible to use WHIP to ingest
> content much faster at lower latencies.

How to store the actual video/audio data is a problem on itself, a relational DB based on SQL could potentially store
metadata and the path/URL to the file and have another server with NGINX or similar that actually holds the files.
There's
plugins for NGINX I believe that support both caching and load balancing in order to make ir more efficient, but a first
version could just include the server itself and storage/retrieval of files logic.

HLS as is might not be enough to have pleasant viewing experience due to higher latencies and initial restrictions that
force
to always have 3 segments in memory before playing the media. Alternatives that solve some of the problems are LL-HLS
and
Apple LL-HLS. Switching from RTMP to WebRTC might also improve the viewing experience.

### Playlist definition

- MUST be UTF-8 encoded.
- MUST be Unicode normalized
- Each line represents something, having the newline character be the divider.
    - Blank lines are ignored.
    - Lines starting with **"#"** are comments or tags, whitespaces MUST NOT be present, unless explicitly expressed.
- Tags begin with **"#EXT"**, they are case-sensitive, all other lines starting with **"#"** are comments and should be
  ignored.
- A URI line identifies a **Media Segment** or another **Playlist file**.
- A **Media segment** is specified by a URI and the tags that apply to it.
- A **Playlist** is a **Master Playlist** if all URI lines in the **Playlist** identify **Media Playlists**.
- A **Playlist** is invalid if it isn't a **Media Playlist** or a **Master Playlist**.
- A URI, whether it is a URI line or part of a tag, MAY be relative. Any relative URI is considered to be relative to
  the
  URI of the **Playlist** that contains it.
- The segment **bit rate** of a **Media Playlist** is the size of the **Media segment** divided by its **EXTINF**
  duration.
- The peak segment **bit rate** of a **Media Playlist** is the largest **bit rate** of any contiguous set of segments
  whose
  total duration is between 0.5 and 1.5 times the target duration. The **bit rate** of a set is calculated by dividing
  the
  sum of the segment sizes by the sum of the segment durations.
- The average segment **bit rate** of a **Media Playlist** is the sum of the sizes (in bits) of every **Media Segment**
  in
  the **Media Playlist**, divided by its duration. Note that this includes container overhead, but not HTTP or other
  overhead
  imposed by the delivery system.

...

### Client/Server responsibilities

#### Server Responsibilities

- The server MUST divide the source media into individual **Media Segments** whose duration is less than or equal to a
  constant target duration.
- The server MUST create a URI for every **Media Segment** that enables its clients to obtain the segment data. If a
  server
  supports partial loading of resources (HTTP Range requests), it MAY specify segments as sub-ranges of larger resources
  using
  the **EXT-X-BYTERANGE** tag.
- Any **Media Segment** specified in the **Playlist** loaded by a client MUST be available for immediate download, or
  playback
  errors can occur. The production of said segment SHOULD finish before it can be downloaded to avoid constraints in its
  transer
  rate.
- HTTP servers SHOULD transfer text files (**Playlist** and **WebVTT segments**) using the "gzip" **Content-Encoding**
  header,
  if the client can accept it.
- The URIs in the **Media Playlist** SHOULD be of **Media segments** that are already available for download, in the
  order
  wished to be played.
- Changes to the **Playlist** file MUST be made atomically from the point of view of the clients (Thread safety?).
  The server MUST NOT change the **Media Playlist** file, except to:
  - Append lines to it.
  - Remove **Media Segment** URIs and tags from the **Playlist** in the order they appear.
  - Increment the value of the **EXT-X-MEDIA-SEQUENCE** or **EXT-X-DISCONTINUITY-SEQUENCE** tags.
  - Add **EXT-X-ENDLIST** tag to the **Playlist**.
  - If **EXT-X-PLAYLIST-TYPE** value is **VOD** indicates that the **Playlist** file MUST NOT change.
  - If **EXT-X-PLAYLIST-TYPE** value is **EVENT** indicates that any part of **Playlist** file MUST NOT be changed or
    removed,
    but it MAY append lines to it.
  - The value of **EXT-X-TARGETDURATION** MUST NOT change.
- Each **Media Segment** has a IDSN (Integer Discontinuity Sequence Number), **EXT-X-DISCONTINUITY-SEQUENCE** tag or
  zero
  if none + **EXT-X-DISCONTINUITY** tag for each segment. This ID in conjunction with the timestamp within the media can
  used
  to synchronize **Media Segments** accross different renditions.
- If the **Media Playlist** contains the final **Media Segment** of the presentation, then the **Playlist** file MUST
  contain
  the **EXT-X-ENDLIST** tag. If a **Media Playlist** does not contain the **EXT-X-ENDLIST** tag, the server MUST make a
  new version
  of the Playlist file available that contains at least one new **Media Segment**. It MUST be made available relative to
  the
  time that the previous version of the **Playlist** file was made available: no earlier than one-half the target
  duration
  after that time, and no later than 1.5 times the target duration after that time. This allows clients to utilize the
  network
  efficiently.
- If the server wishes to remove an entire presentation, it SHOULD provide a clear indication to clients that the *
  *Playlist**
  file is no longer available (e.g., with an HTTP 404 or 410 response). It MUST ensure that all **Media Segments** in
  the **Playlist**
  file remain available to clients for at least the duration of the **Playlist** file at the time of removal to prevent
  interruption
  of in-progress playback.
- The server MAY limit the availability of **Media Segments** by removing them from the **Playlist** file. If segments
  are
  removed the **Playlist** MUST contain an **EXT-X-MEDIA-SEQUENCE** tag. It's value MUST be incremented by 1 for every
  **Media Segment** removed, it MUST NOT decrease or wrap.
- **Media Segments** MUST be removed from the **Playlist** file in the order that they appear.
- The server MUST NOT remove a **Media Segment** from a live **Playlist** if there's less than three segments available.
- When a **Media Segment** URI is removed from the **Playlist**, the corresponding **Media Segment** MUST remain
  available
  to clients for a period of time equal to the duration of the segment plus the longest **Playlist** file distributed by
  the
  server containing that segment. (It's probably best to not calculate this on the fly and just do duration of segment
  plus 24h
  or something similar)
- If the server wishes to remove segments from a **Media Playlist** containing an **EXT-X-DISCONTINUITY** tag, the
  **Media Playlist** MUST contain an **EXT-X-DISCONTINUITY-SEQUENCE** tag. Otherwise, it can be impossible for a client
  to
  locate segments between Renditions.
- If the server removes an **EXT-X-DISCONTINUITY** tag from the **Media Playlist**, it MUST increment the value of the
  **EXT-X-DISCONTINUITY-SEQUENCE** tag so that the Discontinuity Sequence Numbers of the segments still in the **Media
  Playlist**
  remain unchanged. The value of the **EXT-X-DISCONTINUITY-SEQUENCE** tag MUST NOT decrease or wrap.
- If a server plans to remove a **Media Segment** after it is delivered to clients over HTTP, it SHOULD ensure that the
  HTTP response contains an Expires header that reflects the planned time-to-live.
- A live **Playlist** MUST NOT contain the **EXT-X-PLAYLIST-TYPE** tag, as no value of that tag allows **Media Segments
  **
  to be removed.
- **Media Segments** MAY be encrypted. Every encrypted **Media Segment** MUST have an **EXT-X-KEY** tag, with a URI that
  the client can use to obtain a Key file containing the decryption key.
- A **Media Segment** can only be encrypted with one METHOD. However, a server MAY offer multiple ways to retrieve that
  key,
  by providing multiple **EXT-X-KEY** tags, each with a different KEYFORMAT attribute value.
- The server MAY set the HTTP Expires header in the key response to indicate the duration for which the key can be
  cached.
- Any unencrypted **Media Segment** in a **Playlist** that is preceded by an encrypted **Media Segment** MUST have an
  **EXT-X-KEY** tag with a METHOD attribute to NONE. Otherwise, the client will misinterpret those segments as
  encrypted.

(There's more points in relation to encryption of **Media Segments**)

(There's another section for Variant Streams)

