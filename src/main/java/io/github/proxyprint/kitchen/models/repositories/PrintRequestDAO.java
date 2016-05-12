package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest.Status;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by MGonc on 28/04/16.
 */
@Transactional
public interface PrintRequestDAO extends CrudRepository<PrintRequest, Long> {

    public List<PrintRequest> findByStatusInAndPrintshop(List<Status> statuses, PrintShop printshop);
    public PrintRequest findByIdInAndPrintshop(long id, PrintShop printshop);
    public PrintRequest findByIdInAndConsumer(long id, Consumer consumer);

}
