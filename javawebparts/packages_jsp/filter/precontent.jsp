<%
  // The following code is used to test the CompressionFilter.  It retrieves
  // two different images, one uncompressed, two compressed (one with GZIP,
  // one with Deflate) and records the number of bytes received for later
  // display on the page.
  // Get the number of uncompressed bytes retrieved for the CompressFilterTest
  URL u1 = new URL("http" + "://" +
      request.getServerName() + ":" + request.getServerPort() +
      request.getContextPath() + "/img/bigimage1.gif");
  HttpURLConnection h1 = (HttpURLConnection)u1.openConnection();
  h1.connect();
  ByteArrayOutputStream b1 = new ByteArrayOutputStream();
  InputStream i1 = h1.getInputStream();
  while (i1.read() != -1) {
    b1.write((byte)i1.read());
  }
  h1.disconnect();
  int uncompressedBytes = b1.toByteArray().length;
  // Get the number of compressed bytes retrieved for the CompressFilterTest
  // when using GZip compression
  URL u2 = new URL("http" + "://" +
      request.getServerName() + ":" + request.getServerPort() +
      request.getContextPath() + "/img/bigimage2.gif");
  HttpURLConnection h2 = (HttpURLConnection)u2.openConnection();
  h2.setRequestProperty("accept-encoding", "gzip");
  h2.connect();
  ByteArrayOutputStream b2 = new ByteArrayOutputStream();
  InputStream i2 = h2.getInputStream();
  while (i2.read() != -1) {
    b2.write((byte)i2.read());
  }
  h2.disconnect();
  int compressedGZipBytes = b2.toByteArray().length;
  // Get the number of compressed bytes retrieved for the CompressFilterTest
  // when using Deflate compression
  URL u3 = new URL("http" + "://" +
      request.getServerName() + ":" + request.getServerPort() +
      request.getContextPath() + "/img/bigimage2.gif");
  HttpURLConnection h3 = (HttpURLConnection)u3.openConnection();
  h3.setRequestProperty("accept-encoding", "deflate");
  h3.connect();
  ByteArrayOutputStream b3 = new ByteArrayOutputStream();
  InputStream i3 = h3.getInputStream();
  while (i3.read() != -1) {
    b3.write((byte)i3.read());
  }
  h3.disconnect();
  int compressedDeflateBytes = b3.toByteArray().length;
%>
<%
  // The following lines get the auto-created dependency beans from the
  // appropriate scope, updates the eye color property of them, and puts them
  // back in the proper scope.  This demonstrates the updateDependency()
  // function of the DependencyFilter.
  DFTestBean bean1 = (DFTestBean)DependencyFilter.getDependency("MyTestBean1",
    request);
  DFTestBean bean2 = (DFTestBean)DependencyFilter.getDependency("MyTestBean2",
    request);
  bean1.setEyeColor("Blue");
  bean2.setEyeColor("Green");
  DependencyFilter.updateDependency("MyTestBean1", bean1, request);
  DependencyFilter.updateDependency("MyTestBean2", bean2, request);
  // Now we get the beans out again, this time so we can display them later.
  DFTestBean dfbean1 = (DFTestBean)DependencyFilter.getDependency("MyTestBean1",
    request);
  DFTestBean dfbean2 = (DFTestBean)DependencyFilter.getDependency("MyTestBean2",
    request);
  DFTestBean dfbean3 = (DFTestBean)DependencyFilter.getDependency("MyTestBean3",
    request);
  DFTestBean dfbean4 = (DFTestBean)DependencyFilter.getDependency("MyTestBean4",
    request);
%>