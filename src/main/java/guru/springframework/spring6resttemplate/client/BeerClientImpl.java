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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    //I already implement this in the config file as default you don't need
//    private static final String BASE_URL = "http://localhost:8080";

    //with the different environment you should only change PATH
    private static final String GET_BEER_PATH = "/api/v1/beer";

    @Override
    public String listBeers(String beerName) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //this is to give opportunity to implement query param
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        //it will search with query param if query param is filled in
        if(beerName != null){
            uriComponentsBuilder.queryParam("beerName", beerName);
        }

        //ResponseEntity<String> I made this because I will take JSON which is String
        ResponseEntity<String> stringResponse =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), String.class);

        System.out.println(stringResponse.getBody());

        //We invoke here Jackson and it will convert JSON to MAP
        ResponseEntity<Map> mapResponse =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), Map.class);

        ResponseEntity<JsonNode> jsonResponse = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), JsonNode.class);

        //First this will go to beers and after beers it will do iteration and bring you all beerNames
        //This is Jackson
        jsonResponse.getBody().findPath("content")
                        .elements().forEachRemaining(node -> {
                    System.out.println(node.get("beerName").asText());
                });

        return stringResponse.getBody();
    }

    @Override
    public Page<BeerDTO> beerDTOImpl() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //this is gonna convert JSON from API to my model in BeerDTO
        ResponseEntity<BeerDTOPageImpl> response = restTemplate.getForEntity(GET_BEER_PATH, BeerDTOPageImpl.class);



        return response.getBody();
    }


}
