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

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    //i defined base url in config
//    public static final String BASE_URL="http://localhost:8080";
    public static final String GET_BEER_PATH="/api/v1/beer";
    @Override
    public Page<BeerDTO> listBeers() {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //We will take as String ResponseEntity<String>
        ResponseEntity<String> stringResponse =
                //I defined the base url in config, spring automatically puts here
                restTemplate.getForEntity(GET_BEER_PATH, String.class);

        //ResponseEntity<Map> in this point we call Jackson and convert from JSON to Java Map
        ResponseEntity<Map> mapResponse =
                restTemplate.getForEntity(GET_BEER_PATH, Map.class);

        //here you are using again Jackson and easily you can navigate inside JSON
        ResponseEntity<JsonNode> jsonResponse =
                restTemplate.getForEntity(GET_BEER_PATH, JsonNode.class);

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
    public Page<BeerDTO> listBeersPage(String beerName) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //UriComponentsBuilder ALLOWS US TO PUT QUERY PARAM. fromPath MEANS YOU GET THE DEFAULT PATH AND YOU PUT QUERY ON IT
        //YOU ARE NOT DEFINING NEW PATH HERE
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        if (beerName != null){
            uriComponentsBuilder.queryParam("beerName", beerName);
        }

        //we are converting from JSON directly to our POJO
        ResponseEntity<BeerDTOPageImpl> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }




}
