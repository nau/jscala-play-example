package models

case class User(
  username: String, 
  email: String
)

case class UserProfile(
  country: String,
  address: Option[String],
  age: Option[Int]
)