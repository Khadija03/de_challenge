import org.apache.spark.SparkConf
import org.apache.spark.sql.{SQLContext,DataFrame, SparkSession}
import org.apache.spark.sql.catalyst.plans.Inner
import org.apache.spark.sql.functions.{col, count, expr, lit, when}
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import org.apache.spark.sql.functions._
import com.datastax.spark.connector.cql.CassandraConnector

object Main extends App {
  println("IT WORKS !")
  val conf = new SparkConf(true)
    .set("spark.cassandra.connection.host", "cassandra")
    .set("spark.cassandra.connection.port", "9042")
    .set("spark.cassandra.auth.username", "cassandra")
    .set("spark.cassandra.auth.password", "cassandra")
    .setMaster("local[*]")
    .setAppName("First Try")

  val spark =
    SparkSession
      .builder
      .config(conf)
      .getOrCreate
  val sc = spark.sparkContext
  val connector = CassandraConnector.apply(conf)
  val session = connector.openSession

  //spark.sparkContext.setLogLevel("WARN")

  val schema = new StructType()
    .add("C_id", IntegerType, true)
    .add("purch_week", IntegerType, true)
    .add("sku", StringType, true)
    .add("promo_cat", StringType, true)
    .add("promo_discount", StringType, true)
    .add("store_id", IntegerType, true)


  val t_logs = spark.read.format("csv")
    .option("sep", ",")
    .option("inferSchema", "true")
    .schema(schema)
    .load("data/t-logs.csv")


  val sales = t_logs.groupBy("sku", "store_id", "purch_week", "promo_cat", "promo_discount")
    .agg(sum("sku").as("units"))


  val BL = sales.filter(t_logs("promo_cat") === "nan" && t_logs("promo_discount") === "nan")
    .groupBy("sku", "store_id")
    .agg(expr("percentile(units, 0.5)").as("baseline"))


  val Lift = sales.join(BL, Seq("sku", "store_id"), Inner.sql)
    .withColumn("promo_lift_percentage", when(sales("promo_cat") === "nan", 1).otherwise(
      col("units").divide(col("baseline") * 100)
    ))
    .withColumn("incremantal_sales_lift", col("units") - col("baseline"))

  val Lift2 = Lift.filter(col("promo_cat") !== "nan")
    .groupBy("sku", "promo_cat", "promo_discount")
    .agg(sum("units") as "total_sales_units_promo_prod")

  val Final = Lift2.join(Lift, Seq("sku", "promo_cat", "promo_discount"), "outer")
  print("The final dataframe is :")
  Final.toDF()
  //Final.show()
  //println(Final.count())
  //println("Number of nans:" , Final.filter("total_sales_units_promo_prod is null").count())
  Final.printSchema()

  Final.write
    .format("org.apache.spark.sql.cassandra")
    .options(Map("table" -> "mydb", "keyspace" -> "first"))
    .mode("append")
    .save()


}