# jwp-shopping-order

# 2단계 구현

## 기능 목록

- 장바구니
  - 장바구니 항목들을 주문한다.
    - 장바구니 항목들이 제거된다.
    - 주문 항목들이 반환된다.
  - 쿠폰을 적용할 수 있다.
- 쿠폰
  - 쿠폰을 사용해 가격을 할인한다.
    - 할인 정책을 사용해 할인한다.
  - 사용된 쿠폰은 다시 사용할 수 없다.
- 할인 정책
  - 정량, 백분율로 할인 할 수 있다.
  - 상품 가격 이상으로 할인할 수 없다.
    - 상품 가격을 0원으로 만든다
- 돈
  - 변경해도, 기존 돈은 변하지 않는다. 
  - 더할 수 있다
  - 뺄 수 있다
  - 어떤 금액을 빼면 음수인지 알 수 있다.
  - 백분율을 구한다
    - 소수점 이하는 버린다
    - 백분율은 0이상 100이하이다
  - 0 이상이다
## 고민

### 항목이 쿠폰을 사용 vs 쿠폰이 항목을 직접 할인
항목이 아무래도 더 큰 개념인 것 같다.
그래서 항목이 쿠폰을 사용해 본인의 상태를 결정하는 것이 나을 것 같다.

돈은 불변 객체라서 가격을 할인해 반환해도 기존 가격엔 영향을 주지 않는다.
하지만, 쿠폰은 한 번만 사용됨을 보장해야 한다.
그래서 쿠폰은 상태를 가변으로 결정했고, 항목은 불변으로 결정했다.

### 할인을 어디에 할 것인가?
선택지는 3개 정도인 것 같다.
- 상품 자체
- 장바구니 항목
- 주문 항목
나는 장바구니 항목은 어차피 제거될 것이며, 변경될 필요가 없다고 생각한다.
따라서 주문 항목을 만들고, 거기에 적용하는 편이 낫다고 생각했다.

```java
Coupon coupon = new Coupon(1L, 10000, DiscountPolicy.FIXED);
CartItem discounted = coupon.discount(item);

Coupon used = item.discountWith(coupon);
```
