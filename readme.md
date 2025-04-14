## Video streaming api - vid

We have a client (streamer) that ingests or uploads the streaming
contents in a given encoded format, for example, H.264 or H.265, via
HTTP, using protocols like RTMP or HLS, let's focus on the latest.

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