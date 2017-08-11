package JSMProducer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Properties;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
/**
 *
Description:JMS�ͻ�����Ϣ������

 */
/*
 * ��standalone-full.xml�е�hornetq-server�ڵ����
 * <hornetq-server> 
     <jms-destinations>
         <jms-queue name="testQueue">
               <entry name="java:jboss/exported/jms/queue/test"/>
               <durable>true</durable>
         </jms-queue>
     </jms-destinations>
   </hornetq-server>
   ��hornetq�����õ�queue ������Ϣ
 * */
public class JMSProducer_WMQ {
    private static final Logger log = Logger.getLogger(JMSProducer_Local.class.getName());
    private static final String DEFAULT_MESSAGE = "���ǵ�һ��JMS��Ϣ.....";
//    private static final String DEFAULT_MESSAGE = "<adapter><header><protocol value=\"syncpoll\"/><cktid value=\"$cktid\"/><trx value=\"ticketasa\"/><method value=\"query_number\"/><reqid value=\"555729616733615502\"/><profile><instance value=\"1\"/><userid value=\"TICKETASA\"/><ip value=\"TICKETASA\"/></profile><msgid value=\"UW9ag3aU$CKTIDticketasa1\"/></header><input><method value=\"query_number\"/></input></adapter>";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/ConnectionFactory";
//    private static final String DEFAULT_CONNECTION_FACTORY = "java:jboss/jms/Ntelagent/RequestQCF"; 
//    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteJmsXA";
//    private static final String DEFAULT_CONNECTION_FACTORY = "java:/RemoteJmsXA";
//    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    
//    private static final String DEFAULT_DESTINATION = "jms/queue/JMSBridgesourceQ";
    private static final String DEFAULT_DESTINATION = "jms/queue/test";
//    private static final String DEFAULT_DESTINATION = "jms/requestqueue/test";
//    private static final String DEFAULT_DESTINATION = "jms/Ntelagent/RequestQ"; 
//    private static final String DEFAULT_DESTINATION = "jms/queue/ExpiryQueue"; 
     
    private static final String DEFAULT_MESSAGE_COUNT = "1";
//    private static final String DEFAULT_USERNAME = "Jmsqueue";
//    private static final String DEFAULT_USERNAME = "ntel";
//    private static final String DEFAULT_PASSWORD = "welcome0!";
//    private static final String DEFAULT_PASSWORD = "asdqwe123!";
     
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
     
//    private static final String PROVIDER_URL = "remote://localhost:4447";
  private static final String PROVIDER_URL = "remote://192.168.0.117:4447";
    
	public static void main(String[] args) throws Exception {
		
      Context context=null;
      Connection connection=null;
      try {
          // ���������ĵ�JNDI����
          log.info("����JNDI���ʻ�����ϢҲ��������Ӧ�÷���������������Ϣ!");
          final Properties env = new Properties();
          env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);// ��KEY��ֵΪ��ʼ��Context�Ĺ�����,JNDI����������
          env.put(Context.PROVIDER_URL, PROVIDER_URL);// ��KEY��ֵΪContext�����ṩ�ߵ�URL.���������ṩ�ߵ�URL
//          env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
//          env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);//Ӧ���û��ĵ�¼��,����.
          // ��ȡ��InitialContext����.
          context = new InitialContext(env);
          log.info("��ʼ��������,'JNDI��������','�����ṩ��URL','Ӧ���û����˻�','����'���.");
          log.info("��ȡ���ӹ���!");
          ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
          log.info("��ȡĿ�ĵ�!");
          Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);
          // ����JMS���ӡ��Ự�������ߺ�������
          connection = connectionFactory.createConnection();
          
          Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          MessageProducer producer = session.createProducer(destination);
          connection.start();
          int count = Integer.parseInt(DEFAULT_MESSAGE_COUNT);
          // �����ض���Ŀ����Ϣ
          TextMessage message = null;
          for (int i = 0; i < count; i++) {
              message = session.createTextMessage(DEFAULT_MESSAGE);
              message.setJMSMessageID("ID:3423422344224");
              producer.send(message);
              log.info("message:"+message);
              log.info("message:"+DEFAULT_MESSAGE);
          }
          // �ȴ�30���˳�
          CountDownLatch latch = new CountDownLatch(1);
          latch.await(30, TimeUnit.SECONDS);
           
      } catch (Exception e) {
          log.severe(e.getMessage());
          throw e;
      } finally {
          if (context != null) {
              context.close();
          }
          // �ر����Ӹ���Ự,�����̺�������
          if (connection != null) {
              connection.close();
          }
      }
    }
}