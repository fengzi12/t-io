package org.tio.websocket.client.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.RequestLine;
import org.tio.utils.SysConst;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * http client中使用
 *
 * @author tanyaowu 2018年7月8日 上午9:36:54
 */
public class HttpRequestEncoder {

  @SuppressWarnings("unused")
  private static Logger log = LoggerFactory.getLogger(HttpRequestEncoder.class);

  /**
   * @param httpRequest
   * @param groupContext
   * @param channelContext
   * @return
   * @author tanyaowu
   */
  public static ByteBuffer encode(
      HttpRequest httpRequest, GroupContext groupContext, ChannelContext channelContext)
      throws UnsupportedEncodingException {
    int bodyLength = 0;
    byte[] body = httpRequest.getBody();
    if (body != null) {
      bodyLength = body.length;
    }
    if (bodyLength > 0) {
      httpRequest.addHeader(
          HttpConst.RequestHeaderKey.Content_Length, Integer.toString(bodyLength));
    }

    RequestLine requestLine = httpRequest.getRequestLine();
    byte[] requestLineBytes = requestLine.toUrlEncodedString(httpRequest.getCharset()).getBytes();

    Map<String, String> headers = httpRequest.getHeaders();
    StringBuilder sb = new StringBuilder(headers.size() * 50);

    Set<Entry<String, String>> headersEntry = headers.entrySet();
    for (Entry<String, String> entry : headersEntry) {
      sb.append(entry.getKey())
          .append(SysConst.COL_STR)
          .append(entry.getValue())
          .append(SysConst.CRLF);
    }
    //		sb.append(SysConst.CRLF);
    byte[] headerBytes = sb.toString().getBytes();

    ByteBuffer buffer =
        ByteBuffer.allocate(requestLineBytes.length + 2 + headerBytes.length + 2 + bodyLength);
    buffer.put(requestLineBytes);
    buffer.put(SysConst.CR_LF);
    buffer.put(headerBytes);
    buffer.put(SysConst.CR_LF);
    if (bodyLength > 0) {
      buffer.put(body);
    }
    buffer.flip();
    return buffer;
  }

  /** @author tanyaowu */
  private HttpRequestEncoder() {}
}
