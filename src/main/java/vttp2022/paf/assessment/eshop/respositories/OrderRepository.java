package vttp2022.paf.assessment.eshop.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CustomerRepository cRepo;
	// TODO: Task 3

	@Transactional(rollbackFor = OrderException.class)
	public Order createInsertOrder(String name) {

		Customer c = cRepo.findCustomerByName(name).get();

		final Order o = new Order();

		String id = UUID.randomUUID().toString().substring(0, 8);
		o.setOrderId(id);
		o.setName(name);
		o.setAddress(c.getAddress());
		o.setEmail(c.getEmail());
		o.setOrderDate(new Date());

		// insert order into db
		jdbcTemplate.update(SQL_INSERT_ORDER,
            o.getOrderId(), o.getName(), o.getAddress(), o.getStatus(), o.getOrderDate());

		return o;
	}

	public boolean insertOrder(Order order) {
		// orderId, name, address, status, orderDate
        return jdbcTemplate.update(SQL_INSERT_ORDER,
            order.getOrderId(), order.getName(), order.getAddress(), order.getStatus(), order.getOrderDate()) > 0;
	}

	public void addLineItems(List<LineItem> items, String orderId) throws Exception {
        // item, quantity, orderId
        List<Object[]> data = items.stream()
            .map(i -> {
                Object[] obj = new Object[3];
                obj[0] = i.getItem();
                obj[1] = i.getQuantity();
                obj[2] = orderId;
                return obj;
            })
            .toList();
            
        // Batch update
        jdbcTemplate.batchUpdate(SQL_INSERT_ITEMS, data);
    }

	public int getPendingOrderCount(String name) {

		name = name.toLowerCase();

		final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_TOTAL_ORDER, name, "pending");

		return rs.getInt("status_count");
	}

	public int getDispatchedOrderCount(String name) {

		name = name.toLowerCase();

		final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_TOTAL_ORDER, name, "dispatched");

		return rs.getInt("status_count");
	}

}
