package JSMProducer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class JMSProducer_Remote {
	private static final Logger log = Logger.getLogger(JMSProducer_Local.class.getName());

	private static final String DEFAULT_MESSAGE = "the 4 message!";
	// xml文件272行
	private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteJmsXA";
	// xml文件293行,初次找JBOSS的JNDI太不容易了
//	private static final String DEFAULT_DESTINATION = "jms/queue/test";
//	private static final String DEFAULT_DESTINATION = "jms/queue/JMSBridgesourceQ";
	private static final String DEFAULT_MESSAGE_COUNT = "1";

//	private static final String DEFAULT_USERNAME = "lion";
//	private static final String DEFAULT_PASSWORD = "123456";
	private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
//	private static final String PROVIDER_URL = "remote://localhost:4447";
	private static final String PROVIDER_URL = "remote://192.168.0.24:4947";

	public static void main(String[] args) throws Exception {
		Context context=null;
		Connection connection=null;
		try {
			// 设置上下文的JNDI查找
			log.info("设置JNDI访问环境信息也就是设置应用服务器的上下文信息!");
			final Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);// 该KEY的值为初始化Context的工厂类,JNDI驱动的类名
			env.put(Context.PROVIDER_URL,  PROVIDER_URL);// 该KEY的值为Context服务提供者的URL.命名服务提供者的URL
//			env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
//			env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);//应用用户的登录名,密码.
			// 获取到InitialContext对象.
			context = new InitialContext(env);
			log.info("初始化上下文,'JNDI驱动类名','服务提供者URL','应用用户的账户','密码'完毕.");

			log.info("获取连接工厂!");
			ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
			log.info("获取目的地!");
			
//			Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);

			// 创建JMS连接、会话、生产者和消费者
//			connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			Queue queue = session.createQueue("targetqueue");
			Queue queue = session.createQueue("ExpiryQueue");
			MessageProducer producer = session.createProducer(queue);
			connection.start();

			int count = Integer.parseInt(DEFAULT_MESSAGE_COUNT);
			// 发送特定数目的消息
			TextMessage message = null;
			for (int i = 0; i < count; i++) {
				message = session.createTextMessage(DEFAULT_MESSAGE);
				producer.send(message);
				log.info("message:"+message);
				log.info("message:"+DEFAULT_MESSAGE);
			}
			// 等待30秒退出
			CountDownLatch latch = new CountDownLatch(1);
			latch.await(30, TimeUnit.SECONDS);
			
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw e;
		} finally {
			if (context != null) {
				context.close();
			}
			// 关闭连接负责会话,生产商和消费者
			if (connection != null) {
				connection.close();
			}
		}
	}

}
