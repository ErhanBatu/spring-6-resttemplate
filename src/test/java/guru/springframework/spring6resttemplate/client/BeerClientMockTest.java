package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//WHY YOU NEED MOCK TEST BECAUSE IN THE REAL TEST (BeerClientImplTest) YOU HAVE TO USE REAL DATA AND END POINT
@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigure;

    //I will return JSON payload with this object
    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO dto;
    String dtoJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigure.build();
        //I initiliaze here new server, I am not using Spring server, basically mimicking
        server = MockRestServiceServer.bindTo(restTemplate).build();
        //i bound the resttemplate to server
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);

        dto = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(dto);
    }

    @Test
    void testListBeersWithQueryPara() throws JsonProcessingException {

        String response = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromHttpUrl(URL+BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "ALE")
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(header("Authorization","Basic dXNlcjE6cGFzc3dvcmQ="))
                .andExpect(queryParam("beerName", "ALE"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient
                .listBeers("ALE", null, null,null,null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);


    }

    @Test
    void testCreateBeer() {

        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
                        .build(dto.getId());

        server.expect(method(HttpMethod.POST))
                        .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                                .andRespond(withAccepted().location(uri));

        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));


        BeerDTO responseDto = beerClient.createBeer(dto);
        assertThat(responseDto.getId()).isEqualTo(dto.getId());


    }


    @Test
    void testGetById(){

        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL+BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));

        BeerDTO responseDto = beerClient.getBeerById(dto.getId());
        assertThat(responseDto.getId()).isEqualTo(dto.getId());

    }


    @Test
    void testListBeers() throws JsonProcessingException {

        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL+BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();
        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    BeerDTO getBeerDto(){
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("12345")
                .build();
    }

    BeerDTOPageImpl getPage(){
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }
}
