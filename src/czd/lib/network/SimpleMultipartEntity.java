package czd.lib.network;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SimpleMultipartEntity implements HttpEntity {
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	private static final byte[] CR_LF = ("\r\n").getBytes();
	private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n".getBytes();

	private String boundary = null;
	private byte[] boundaryLine;
	private byte[] boundaryEnd;

	private List<FilePart> fileParts = new ArrayList<FilePart>();

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	private AsyncHttpResponseHandler progressHandler;
	private int bytesWritten = 0;
	private int totalSize = 0;

	public SimpleMultipartEntity(AsyncHttpResponseHandler progressHandler) {
		final StringBuffer buf = new StringBuffer();
		final Random rand = new Random();
		for (int i = 0; i < 30; i++)
		{
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = buf.toString();
		this.boundaryLine = ("--" + boundary + "\r\n").getBytes();
		this.boundaryEnd = ("--" + boundary + "--\r\n").getBytes();

	}

	public void addPart(final String key, final String value) {
		addPart(key, value, "text/plain; charset=UTF-8");
	}

	public void addPart(final String key, final String value, final String contentType) {
		try
		{
			out.write(boundaryLine);
			out.write(createContentDisposition(key));
			out.write(CR_LF);
			out.write(value.getBytes());
			out.write(CR_LF);
		} catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public void addPart(final String key, File file) {
		addPart(key, file, null);
	}

	public void addPart(final String key, File file, String type) {
		if (type == null)
		{
			type = APPLICATION_OCTET_STREAM;
		}
		fileParts.add(new FilePart(key, file, type));
	}

	public void addPart(final String key, String streamName, InputStream inputStream, String type) throws IOException {
		if (type == null)
		{
			type = APPLICATION_OCTET_STREAM;
		}
		out.write(boundaryLine);
		out.write(createContentDisposition(key, streamName));
		out.write(createContentType(type));
		out.write(TRANSFER_ENCODING_BINARY);
		out.write(CR_LF);

		final byte[] tmp = new byte[4096];
		int k = 0;
		while ((k = inputStream.read(tmp, 0, 4096)) != -1)
		{
			out.write(tmp, 0, k);
		}
		out.write(CR_LF);
		out.flush();
		try
		{
			inputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private byte[] createContentType(String type) {
		String result = "Content-Type:" + type + "\r\n";
		return result.getBytes();
	}

	private byte[] createContentDisposition(final String key) {
		String result = "Content-Disposition: form-data; name=\"" + key + "\"\r\n";
		return result.getBytes();
	}

	private byte[] createContentDisposition(final String key, final String fileName) {
		String result = "Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n";
		return result.getBytes();
	}

	private void updateProgress(int count) {
		bytesWritten += count;
		progressHandler.sendProgressMessage(bytesWritten, totalSize);
	}

	@Override
	public long getContentLength() {
		long contentLen = out.size();
		for (FilePart filePart : fileParts)
		{
			long len = filePart.getTotalLength();
			if (len < 0)
			{
				return -1;
			}
			contentLen += len;
		}
		contentLen += boundaryEnd.length;
		return contentLen;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		bytesWritten = 0;
		totalSize = (int)getContentLength();
		out.writeTo(outstream);
		updateProgress(out.size());

		for (FilePart filePart : fileParts)
		{
			filePart.writeTo(outstream);
		}
		outstream.write(boundaryEnd);
		updateProgress(boundaryEnd.length);
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public void consumeContent() throws IOException, UnsupportedOperationException {
		if (isStreaming())
		{
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
		}
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		//writeLastBoundaryIfNeeds();
		//return new ByteArrayInputStream(out.toByteArray());
		throw new UnsupportedOperationException("getContent() is not supported. Use writeTo() instead.");
	}

	private class FilePart {
		public File file;
		public byte[] header;

		public FilePart(String key, File file, String type) {
			header = createHeader(key, file.getName(), type);
			this.file = file;
		}

		private byte[] createHeader(String key, String filename, String type) {
			ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
			try
			{
				headerStream.write(boundaryLine);

				headerStream.write(createContentDisposition(key, filename));
				headerStream.write(createContentType(type));
				headerStream.write(TRANSFER_ENCODING_BINARY);
				headerStream.write(CR_LF);
			} catch (IOException e)
			{
				// Can't happen on ByteArrayOutputStream
			}
			return headerStream.toByteArray();
		}

		public long getTotalLength() {
			long streamLength = file.length();
			return header.length + streamLength;
		}

		public void writeTo(OutputStream out) throws IOException {
			out.write(header);
			updateProgress(header.length);
			FileInputStream inputStream = new FileInputStream(file);
			byte[] tmp = new byte[4096];
			int l = 0;
			while ((l = inputStream.read(tmp, 0, 4096)) != -1)
			{
				out.write(tmp, 0, l);
				updateProgress(l);
			}
			out.write(CR_LF);
			updateProgress(CR_LF.length);
			out.flush();
			try
			{
				inputStream.close();
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
