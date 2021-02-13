package com.assignment.spring;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.assignment.spring.api.Main;
import com.assignment.spring.api.Sys;
import com.assignment.spring.api.WeatherResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private RestTemplate restTemplate;
	
	private ObjectMapper objectMapper=new ObjectMapper();
	
	@Autowired
	WeatherRepository weatherRepository;
	@Autowired
	WeatherController weatherController;
	private MockRestServiceServer mockServer;
	
	@Before
	public void initd()
	{
		mockServer=MockRestServiceServer.createServer(restTemplate);
	}
	@Test
	public void test_weather() throws JsonProcessingException, URISyntaxException
	{
		String url = Constants.WEATHER_API_URL.replace("{city}", "London").replace("{appid}", Constants.APP_ID);
		
		Main main=new Main();
		main.setTemp(10.0);
		Sys sys=new Sys();
		sys.setCountry("UK");
		
		WeatherResponse weatherResponse=new WeatherResponse();
		weatherResponse.setName("London");
		weatherResponse.setMain(main);
		weatherResponse.setSys(sys);
		
		ResponseEntity<WeatherResponse> weatherResponseEntity=new ResponseEntity(weatherResponse,HttpStatus.OK);
		HttpServletRequest mockRequest=mock(HttpServletRequest.class);
		
		when(mockRequest.getParameter("city")).thenReturn("London");
		mockServer.expect(ExpectedCount.once(),requestTo(new URI(url))).
		andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).
		contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(weatherResponseEntity.getBody())));
		
		weatherController.weather(mockRequest);
		WeatherEntity responseEntity=weatherRepository.findById(1).get();
		assertEquals("London",responseEntity.getCity());
		assertEquals("UK",responseEntity.getCountry());
        assertEquals(Double.valueOf(10.0),responseEntity.getTemperature());

	}
	
	
	
	

}
