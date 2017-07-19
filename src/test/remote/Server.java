package remote;

import com.pc.base.BaseResult;
import com.pc.base.ReturnCode;
import com.pc.core.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by panrui on 2015/9/13.
 */
public class Server {
    public static final ThreadGroup respGroup = new ThreadGroup("server");
    public static String serverIp ;//TODO:afterPorperties
    public static int serverPort ;
    public static Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            run();
        }
    };
    private static final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(respGroup, r);
        }

    });

    public static void service() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(serverIp, serverPort));
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
        System.out.println("Server started....");
        while (selector.select() > 0) {
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey sk = iter.next();
                iter.remove();

                if (sk.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) sk.channel();
                    SocketChannel sc = server.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    SocketChannel channel = (SocketChannel) sk.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(2048);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while (channel.read(buffer) > 0) {
                        buffer.flip();
                        bos.write(buffer.array(), buffer.position(), buffer.limit());
                        buffer.clear();
                    }
                    Future<Map<String,Object>> returnCodeFuture = pool.submit(new GetResult(bos.toByteArray()));
                    Map<String,Object> resutlData = null;
                    try {
                        resutlData = returnCodeFuture.get(3500l, TimeUnit.MILLISECONDS);
                        channel.write(ByteBuffer.wrap(SerializeUtil.serialize(resutlData)));
                    } catch (InterruptedException e) {
                        if (Thread.currentThread().isInterrupted()) Thread.currentThread().start();
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        channel.write(ByteBuffer.wrap(SerializeUtil.serialize(new BaseResult(ReturnCode.Server_Exec_Error))));
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        channel.write(ByteBuffer.wrap(SerializeUtil.serialize(new BaseResult(ReturnCode.Server_Exec_Timeout))));
                    }
                    channel.shutdownOutput();
                    sk.cancel();
                }
            }
        }
    }

    static class GetResult implements Callable<Map<String,Object>> {
        private byte[] bytes;

        public GetResult(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public Map<String,Object> call() throws Exception {
            Map<String,Object> rService = (Map) SerializeUtil.unserialize(bytes);
            Object service = Constants.applicationContext.getBean((String) rService.remove("sBean"));
/*            int len = serviceType.getArgs() != null ? serviceType.getArgs().length : 0;
            Class[] paramsClass = new Class[len];
            for (int i = 0; i < len; i++) {
                paramsClass[i] = serviceType.getArgs()[i].getClass();
            }*/
            return (Map<String,Object>) service.getClass().getDeclaredMethod((String) rService.remove("sMethod"), rService.getClass()).invoke(service, rService);
        }
    }

    public static void run() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "server");
        thread.setDaemon(true);
        thread.start();
    }
}
