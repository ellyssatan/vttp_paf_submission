package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.OrderException;

@Service
public class WarehouseService {

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	@Transactional(rollbackFor = OrderException.class)
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4

		JsonArrayBuilder itemsArray = Json.createArrayBuilder();

		for (LineItem i : order.getLineItems()) {
			itemsArray.add(Json.createObjectBuilder()
							.add("item", i.getItem())
							.add("quantity", i.getQuantity()).build());
		}

		JsonObject json = Json.createObjectBuilder()
							.add ("orderId", order.getOrderId())
							.add ("name", order.getName())
							.add ("address", order.getAddress())
							.add ("email", order.getEmail())
							.add ("lineItems", itemsArray.build())
							.add ("createdBy", "Tan Jia Yi Ellyssa")
							.build();
							
		RequestEntity<String> req = RequestEntity
									.post("http://paf.chuklee.com/dispatch/" + order.getOrderId())
									.contentType (MediaType.APPLICATION_JSON)
									// .headers("Accept", MediaType.APPLICATION_JSON)
									.body(json.toString(), String.class);

		RestTemplate template = new RestTemplate() ;

		ResponseEntity<String> resp = template.exchange(req, String.class);

		String payload = resp.getBody();

		JsonReader reader = Json.createReader(new StringReader(payload));
		JsonObject jo = reader.readObject();

		OrderStatus os = new OrderStatus();

		os.setOrderId(order.getOrderId());
		os.setDeliveryId(jo.getString("deliveryId") != null ? jo.getString("deliveryId") : "");
		os.setStatus(jo.getString("deliveryId") != null ? "dispatched" : "pending");
		
		return os;
	}
}
