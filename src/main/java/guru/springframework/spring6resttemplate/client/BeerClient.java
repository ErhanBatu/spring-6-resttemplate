package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public interface BeerClient {

    //I take Page from Spring, if you want you can create your own properties
    Page<BeerDTO> listBeers();

}
