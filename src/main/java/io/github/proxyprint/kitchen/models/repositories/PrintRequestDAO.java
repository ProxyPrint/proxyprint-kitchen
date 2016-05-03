package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintRequest.Status;
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
}
