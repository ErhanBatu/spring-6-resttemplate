package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    public static final String BASE_URL="http://localhost:8080";
    public static final String GET_BEER_PATH="/api/v1/beer";
    @Override
    public Page<BeerDTO> listBeers() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //We will take as String ResponseEntity<String>
        ResponseEntity<String> stringResponse =
                restTemplate.getForEntity(BASE_URL+GET_BEER_PATH, String.class);

        //ResponseEntity<Map> in this point we call Jackson and convert from JSON to Java Map
        ResponseEntity<Map> mapResponse =
                restTemplate.getForEntity(BASE_URL+GET_BEER_PATH, Map.class);

        //here you are using again Jackson and easily you can navigate inside JSON
        ResponseEntity<JsonNode> jsonResponse =
                restTemplate.getForEntity(BASE_URL+GET_BEER_PATH, JsonNode.class);

        System.out.println(stringResponse.getBody());

        //it will give you as map
        System.out.println(mapResponse.getBody());

        //it will return all beerName
        jsonResponse.getBody().findPath("content")
                .elements().forEachRemaining(node->{
                        System.out.println(node.get("beerName").asText());
                });

        return null;
    }

    @Override
    public Page<BeerDTO> listBeersPage() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //we are converting from JSON directly to our POJO
        ResponseEntity<BeerDTOPageImpl> stringResponse =
                restTemplate.getForEntity(BASE_URL+GET_BEER_PATH, BeerDTOPageImpl.class);

        return null;
    }




}
