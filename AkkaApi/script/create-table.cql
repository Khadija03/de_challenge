create keyspace if not exists first WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
use first;
create table mydb (sku varchar,promo_cat varchar, total_sales_units_promo_prod bigint, store_id int, purch_week int, promo_discount varchar, units bigint, baseline double, incremantal_sales_lift double,promo_lift_percentage double, PRIMARY KEY (sku,store_id,purch_week));