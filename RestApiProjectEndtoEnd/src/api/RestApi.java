package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import pojo.AuthReq;
import pojo.AuthRes;
import pojo.GBooking;
import pojo.PostCBookingDatesreq;
import pojo.PostCreateBookingres;
import pojo.PostCreatereq;
import pojo.PostCreateres;
import pojo.GetBookingres;

public class RestApi {

	@Test
	public void EndtoEnd() throws JsonMappingException, JsonProcessingException {

		// Auth-CreateToken

		System.out.println("##########AUTH REQUEST############");
		RequestSpecification reqBase = new RequestSpecBuilder().setBaseUri("https://restful-booker.herokuapp.com")
				.setContentType(ContentType.JSON).build();

		AuthReq authreq = new AuthReq();
		authreq.setUsername("admin");
		authreq.setPassword("password123");

		RequestSpecification reqAuth = given().log().all().spec(reqBase).body(authreq);

		AuthRes resAuth = reqAuth.when().post("auth").then().statusCode(200).extract().as(AuthRes.class);

		String token = "token=" + resAuth.getToken();
		// String tokenn = ("token="+token1);
		System.out.println(token);

		Assert.assertNotNull(token, "Token should not be null");
		Assert.assertTrue(token.startsWith("token="), "Token should start with 'token='");

		// Create Book ---->> POST

		System.out.println("##########HTTP POST REQUEST############");

		PostCreatereq postcreatereq = new PostCreatereq();

		postcreatereq.setFirstname("Osman");
		postcreatereq.setLastname("Bey");
		postcreatereq.setTotalprice("45000");
		postcreatereq.setDepositpaid("True");

		PostCBookingDatesreq postbookingdatesreq = new PostCBookingDatesreq();
		postbookingdatesreq.setCheckin("2024-02-04");
		postbookingdatesreq.setCheckout("2024-02-07");

		postcreatereq.setBookingdates(postbookingdatesreq);
		postcreatereq.setAdditionalneeds("Dinner");

		RequestSpecification reqPost = given().spec(reqBase).body(postcreatereq);

		PostCreateres postCreateres = reqPost.when().post("booking").then().log().all().statusCode(200).extract()
				.as(PostCreateres.class);

		int bookingid = postCreateres.getBookingid();
		System.out.println(bookingid);

		// GET Booking

		System.out.println("##########HTTP GET REQUEST ALL ID WILL COME############");
		RequestSpecification reqGetAll = given().log().all().spec(reqBase);

		ExtractableResponse<Response> resGetAll = reqGetAll.when().get("booking").then().statusCode(200).extract();

		// GetID

		System.out.println("##########HTTP GET REQUEST FOR SPECIFIC BOOKINGID############");

		ExtractableResponse<Response> resGetId = reqGetAll.when().get("booking/" + bookingid).then().statusCode(200)
				.log().all().extract();

		// Update

		System.out.println("##########HTTP PUT REQUEST############");
		RequestSpecification reqBaseUpdate = new RequestSpecBuilder().setBaseUri("https://restful-booker.herokuapp.com")
				.setContentType(ContentType.JSON).addHeader("Cookie", token).addHeader("Accept", "application/json")
				.build();

		postcreatereq.setFirstname("Umer");
		postcreatereq.setLastname("Bey");
		postcreatereq.setTotalprice("55000");
		postcreatereq.setDepositpaid("True");

		postbookingdatesreq.setCheckin("2024-02-05");
		postbookingdatesreq.setCheckout("2024-02-08");

		postcreatereq.setBookingdates(postbookingdatesreq);
		postcreatereq.setAdditionalneeds("Dinner");

		RequestSpecification reqUpdate = given().log().all().spec(reqBaseUpdate).contentType(ContentType.JSON)
				.body(postcreatereq);

		ExtractableResponse<Response> resUpdate = reqUpdate.when().put("booking/" + bookingid).then().log().all()
				.extract();

		// GET after Updating

		System.out.println("##########HTTP GET REQUEST AFTER UPDATE############");

		ExtractableResponse<Response> resGetUpdatedId = reqGetAll.when().get("booking/" + bookingid).then().log().all()
				.extract();

		// Delete

		System.out.println("##########HTTP DELETE REQUEST############");

		RequestSpecification reqBaseDelete = new RequestSpecBuilder().setBaseUri("https://restful-booker.herokuapp.com")
				.setContentType(ContentType.JSON).addHeader("Cookie", token)
				.addHeader("Content-Type", "application/json").build();

		RequestSpecification reqDelete = given().log().all().spec(reqBaseDelete);

		ExtractableResponse<Response> resDelete = reqDelete.when().delete("booking/" + bookingid).then().log().all()
				.extract();

	}

}