package de.factoryfx.testutils;

import java.rmi.registry.LocateRegistry;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**utility class to ensure that only one instance of the application is started*/
public class SingleProcessInstanceUtil {

	public interface KillMXBean {
		void kill();
	}

	public static class KillMBeanImpl implements KillMXBean {

		Runnable killAction;
		public KillMBeanImpl(){
			killAction= () -> {
				System.exit(0);
			};
		}

		public KillMBeanImpl(Runnable killAction){
			this.killAction=killAction;
		}

		@Override
		public void kill() {
			killAction.run();
		}
	}
	private static boolean created=false;


	public static void enforceSingleProcessInstance(int port) {
		enforceSingleProcessInstance(port,null);
	}
	/**
	 * @param port e.g 1099
	 */
	public static void enforceSingleProcessInstance(int port, Runnable killAction) {
		if (created){
			return;
		}
		//close old instances to free resource
		
		ObjectName name;
		try {
			name = new ObjectName("killUtil:type=Kill");
		} catch (MalformedObjectNameException e1) {
			throw new RuntimeException(e1);
		}
		
		try {

			JMXServiceURL u = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://localhost:"+port+"/jmxrmi" );
			JMXConnector c = JMXConnectorFactory.connect( u );
			MBeanServerConnection mbsc = c.getMBeanServerConnection();
			KillMXBean mbeanProxy = JMX.newMBeanProxy(mbsc, name, KillMXBean.class, true);
			mbeanProxy.kill();
		} catch (Exception e1) {
			//Ignore
		}
		
		try {
			LocateRegistry.createRegistry(port);
			MBeanServer server = MBeanServerFactory.createMBeanServer();
			String url = "service:jmx:rmi:///jndi/rmi://localhost:"+port+"/jmxrmi";
			JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
			                                       new JMXServiceURL( url), null, server );
			connectorServer.start();

			KillMBeanImpl maze;
			if (killAction==null){
				maze = new KillMBeanImpl();
			} else {
				maze = new KillMBeanImpl(killAction);
			}
			server.registerMBean( maze, name );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		created=true;
	}
}
