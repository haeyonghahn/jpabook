## 글로벌 페치 전략 설정
`@OneToMany` 와 `@ManyToMany` 는 기본이 `지연로딩` 이다. `@OneToOne` 과 `@ManyToOne` 을 `fetch` 속성을 이용하여 지연 로딩으로 수정하자. 찾아보면 `Order` 엔티티와 `OrderItem` 엔티티에 존재하므로 해당 엔티티를 수정하자.    

=== 주문( `Order` ) ===    
```java
...
@ManyToOne(fetch = FetchType.LAZY) //**
@JoinColumn(name = "MEMBER_ID")
private Member member; //주문 회원

@OneToOne(fetch = FetchType.LAZY) //**
@JoinColumn(name = "DELIVERY_ID")
private Delivery delivery; //배송정보
...
```
`member` 와 `delivery` 를 지연 로딩으로 설정했다.

=== 주문( `OrderItem` ) ===    
```java
...
@ManyToOne(fetch = FetchType.LAZY) //**
@JoinColumn(name = "ITEM_ID")
private Item item; //주문 상품

@ManyToOne(fetch = FetchType.LAZY) //**
@JoinColumn(name = "ORDER_ID")
private Order order; //주문
...
```
`item` 과 `order` 를 지연 로딩으로 설정했다.   

## 영속성 전이 설정
엔티티를 영속 상태로 만들어서 데이터베이스에 저장할 때 연관된 엔티티도 모두 영속 상태여야 한다. 연관된 엔티티 중에 영속 상태가 아닌 엔티티가 있으면 예외가 발생한다. (정확히는 플러시 시점에 오류가 발생한다.) 영속성 전이를 사용하면 연관된 엔티티를 편리하게 영속 상태로 만들 수 있다.

=== 주문( `Order` ) ===    
```java
...
@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY) //** <1>
@JoinColumn(name = "DELIVERY_ID")
private Delivery delivery; //배송정보

@OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //** <2>
private List<OrderItem> orderItems = new ArrayList<OrderItem>();

public void addOrderItem(OrderItem orderItem) {
 orderItems.add(orderItem);
 orderItem.setOrder(this);
}
public void setDelivery(Delivery delivery) {
 this.delivery = delivery;
 delivery.setOrder(this);
}
...
```
- <1> : `Order` -> `Delivery` 관계인 `delivery` 필드에 `cascade = CascadeType.ALL` 로 영속성 전이를 설정했다.
- <2> : `Order` -> `OrderItem` 관계인 `orderItems` 필드에 `cascade = CascadeType.ALL` 로 영속성 전이를 설정했다.

=== 영속성 전이를 사용하기 전 ===   
```java
Delivery delivery = new Delivery();
em.persist(delivery); //persist //**

OrderItem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();
em.persist(orderItem1); //persist //**
em.persist(orderItem2); //persist //**

Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);
em.persist(order); //persist //**
```
영속성 전이를 사용하기 전에는 연관된 엔티티들을 직접 영속 상태로 만들어야 한다.

=== 영속성 전이를 사용한 후 ===    
```java
Delivery delivery = new Delivery();
OrderItem orderItem1 = new OrderItem();
OrderItem orderItem2 = new OrderItem();
Order order = new Order();
order.setDelivery(delivery);
order.addOrderItem(orderItem1);
order.addOrderItem(orderItem2);
em.persist(order); //delivery, orderItems 플러시 시점에 영속성 전이
```
`Order` 만 영속 상태로 만들면 영속성 전이로 설정한 `delivery` `orderItem` 도 영속 상태가 된다. 참고로 `persist` 는 플러시 시점에 영속성 전이가 일어난다. 
