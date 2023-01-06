package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    public static final String SQL_FIND_CUSTOMER_BY_NAME = "select name, address, email from customers where name = ?";
    public static final String SQL_INSERT_ITEMS =
        "insert into line_item (item, quantity, orderId) values (?, ?, ?);";
    public static final String SQL_INSERT_ORDER =
        "insert into orders (orderId, name, address, status, orderDate) value (?, ?, ?, ?, ?)";
    public static final String SQL_GET_TOTAL_ORDER = """
        select c.name, count(o.status) as status_count
        from
            customers c
            join orders o
            on c.name = o.name
        where
            c.name = ?
            and
            o.status = ?
            """;

}
