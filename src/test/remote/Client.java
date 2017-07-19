package remote;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by panrui on 2015/9/13.
 */
public class Client {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    public static String serverIp ;//TODO:afterPorperties
    public static int serverPort ;
    public static Future<Map<String,Object>> getService(Map<String,Object> params) {
        return executorService.submit(new GetServer(params));
    }

   public static class GetServer implements Callable<Map<String,Object>> {
        private Map<String,Object> params;

        public GetServer( Map<String,Object> params) {
            this.params = params;
        }

        @Override
        public Map<String,Object> call() throws Exception {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(new InetSocketAddress(serverIp, serverPort));
            Selector selector = Selector.open();
            sc.register(selector, SelectionKey.OP_CONNECT);
            Map<String,Object> obj = null;
            while (selector.select() > 0) {
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey sk = iter.next();
                    iter.remove();
                    if (sk.isConnectable()) {
                        if (sc.isConnectionPending())
                            sc.finishConnect();
//                        sc.write(ByteBuffer.wrap(clazz.getSimpleName().getBytes("UTF-8")));
                        sc.write(ByteBuffer.wrap(SerializeUtil.serialize(params)));
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (sk.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(2048);
                        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                        while (sc.read(buffer) > 0) {
                            buffer.flip();
                            byteArr.write(buffer.array(),buffer.position(),buffer.limit());
                            buffer.clear();
                        }
                        byte[] data=byteArr.toByteArray();
                        byteArr.flush();
                            obj = (Map<String,Object>) SerializeUtil.unserialize(data);
                        sk.cancel();
                        sc.close();
                        selector.close();
                    return obj;
                    }
                }
//                    selector.close();
            }
            return obj;
        }
    }
}
