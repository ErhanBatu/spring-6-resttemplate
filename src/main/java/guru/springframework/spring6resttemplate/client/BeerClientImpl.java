package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;

    //i defined base url in config
//    public static final String BASE_URL="http://localhost:8080";
    public static final String GET_BEER_PATH="/api/v1/beer";

    public static final String GET_BEER_BY_ID_PATH="/api/v1/beer/{beerId}";


    @Override
    public BeerDTO createBeer(BeerDTO newDto) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        //you can use this one below or URI
//        ResponseEntity<BeerDTO> response = restTemplate.postForEntity(GET_BEER_PATH, newDto,BeerDTO.class);

        //with URI you can get the response header
        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);

        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {

        //we put the parameter to url, this is not query param
        RestTemplate restTemplate = restTemplateBuilder.build();

        //this is gonna return back BeerDTO.class, Jackson will handle this parsing
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }


    @Override
    public Page<BeerDTO> myBeers() {

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
                .elements().forEachRemaining(node -> {
                    System.out.println(node.get("beerName").asText());
                });

        return null;
    }


        public Page<BeerDTO> listBeers () {
            return this.listBeers(null, null, null, null, null);
        }



        @Override
        public Page<BeerDTO> listBeers (String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                Integer pageSize){
            RestTemplate restTemplate = restTemplateBuilder.build();

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

            if (beerName != null) {
                uriComponentsBuilder.queryParam("beerName", beerName);
            }

            if (beerStyle != null) {
                uriComponentsBuilder.queryParam("beerStyle", beerStyle);
            }

            if (showInventory != null) {
                uriComponentsBuilder.queryParam("showInventory", beerStyle);
            }

            if (pageNumber != null) {
                uriComponentsBuilder.queryParam("pageNumber", beerStyle);
            }

            if (pageSize != null) {
                uriComponentsBuilder.queryParam("pageSize", beerStyle);
            }


            ResponseEntity<BeerDTOPageImpl> response =
                    restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);


            return response.getBody();
        }

}
