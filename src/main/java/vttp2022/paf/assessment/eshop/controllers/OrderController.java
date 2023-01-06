package vttp2022.paf.assessment.eshop.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@RestController
@RequestMapping
public class OrderController {

	@Autowired
    private CustomerRepository cRepo;

	@Autowired
    private OrderRepository orderRepo;

	@Autowired
    private WarehouseService warehouseService;
    
    @GetMapping("/search")
    public ResponseEntity<String> findCustomerByName(@RequestParam String name) {

        // String name = form.getFirst("name");
        Optional<Customer> c = cRepo.findCustomerByName(name);

        if (c.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)
                .body(Json.createObjectBuilder().add("error", "Customer " + name + " not found")
                .build().toString());
        }

        Customer customer = c.get();
        JsonObject jo = Json.createObjectBuilder()
                            .add("name", customer.getName())
                            .add("address", customer.getAddress())
                            .add("email", customer.getEmail())
                            .build();

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(jo.toString());
    }

	@PostMapping
	public ResponseEntity<String> dispatchOrder(@RequestBody MultiValueMap<String, String> form) {

		String name = form.getFirst("name");
		String lineItems = form.getFirst("items");



		Order o = orderRepo.createInsertOrder(name);
		// orderRepo.addLineItems(lineItems, o.getOrderId());


		return null;
	}
	@PostMapping(consumes="application/json")
	public ResponseEntity<String> postDispatch(@RequestBody MultiValueMap<String, String> form) {

		String name = form.getFirst("name");
		Order o = orderRepo.createInsertOrder(name);

		OrderStatus os = warehouseService.dispatch(o);

		JsonObject jo;

		if ("dispatched" == os.getStatus()) {
			jo = Json.createObjectBuilder()
				.add("orderId", o.getOrderId())
				.add("deliveryId", o.getDeliveryId())
				.add("status", "dispatched")
				.build();
		}

		jo = Json.createObjectBuilder()
				.add("orderId", o.getOrderId())
				.add("status", "pending")
				.build();
		
		return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(jo.toString());
	}

	@GetMapping(path = "/api/order/{name}/status")
	public ResponseEntity<String> getTotalOrders(@PathVariable("name") String name) {

		int dispatchedOrders = orderRepo.getDispatchedOrderCount(name);
		int pendingOrders = orderRepo.getPendingOrderCount(name);

		JsonObject jo = Json.createObjectBuilder()
				.add("name", name)
				.add("dispatched", dispatchedOrders)
				.add("pending", pendingOrders)
				.build();
		
		return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(jo.toString());
	}
}
