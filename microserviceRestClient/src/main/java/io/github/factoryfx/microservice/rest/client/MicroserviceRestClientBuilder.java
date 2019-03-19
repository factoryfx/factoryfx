package io.github.factoryfx.microservice.rest.client;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.server.user.persistent.PasswordHash;


public class MicroserviceRestClientBuilder {


    /**
     * For user management
     * @param host host
     * @param port port
     * @param user user
     * @param passwordNotHashed password in clear text
     * @param serverRootClass  server class, cause type erasure
     * @param <R>  Root client
     * @param <RS> Root Server
     * @param <S> Summary Data for factory history
     * @return client
     */
    public static <R extends FactoryBase<?,R>, RS extends FactoryBase<?,RS>,S> MicroserviceRestClient<RS,S> build(String host, int port, String user, String passwordNotHashed, Class<RS> serverRootClass){
        MicroserviceRestClientFactory<R, RS,S> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();
        microserviceRestClientFactory.port.set(port);
        microserviceRestClientFactory.host.set(host);
        microserviceRestClientFactory.user.set(user);
        microserviceRestClientFactory.passwordHash.set(new PasswordHash().hash(passwordNotHashed));
        return microserviceRestClientFactory.createClient();
    }

    /**
     *  For no user management
     * @param host host
     * @param port port
     * @param serverRootClass server class, cause type erasure
     * @param <R>  Root client
     * @param <RS> Root Server
     * @param <S> Summary Data for factory history
     * @return client
     */
    public static <R extends FactoryBase<?,R>, RS extends FactoryBase<?,RS>,S> MicroserviceRestClient<RS,S> build(String host, int port, Class<RS> serverRootClass){
        MicroserviceRestClientFactory<R, RS,S> microserviceRestClientFactory = new MicroserviceRestClientFactory<>();
        microserviceRestClientFactory.port.set(port);
        microserviceRestClientFactory.host.set(host);
        microserviceRestClientFactory.user.set(null);
        microserviceRestClientFactory.passwordHash.set(null);
        return microserviceRestClientFactory.createClient();
    }
}
