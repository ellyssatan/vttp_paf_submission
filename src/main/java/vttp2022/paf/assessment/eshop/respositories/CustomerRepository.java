package vttp2022.paf.assessment.eshop.respositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2022.paf.assessment.eshop.models.Customer;
import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

@Repository
public class CustomerRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// You cannot change the method's signature
	public Optional<Customer> findCustomerByName(String name) {
		// TODO: Task 3

		String search = name.toLowerCase();

		final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_FIND_CUSTOMER_BY_NAME, search);
		
		if (rs.next()) {
			Customer c = new Customer();
			c.setName(rs.getString("name"));
			c.setAddress(rs.getString("address"));
			c.setEmail(rs.getString("email"));
			System.out.println(c.toString());
			return Optional.of(c);
		}
		
		return Optional.empty();
	}
}
