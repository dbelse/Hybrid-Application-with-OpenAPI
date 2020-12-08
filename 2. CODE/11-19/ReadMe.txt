Test_Server(eclipse) :
스레드 기반 mulit-server

안드로이드에서 인증키 생성시 server에서 send, recv 쓰레드 생성하여 recv쓰레드에서 인증키-전화번호 순으로 받고 이를 HashMap에 저장
send쓰레드에서는 while문을 돌면서 hashmap에 인증키 값이 있는지 확인하고 없다면 connect가 완료된것

안드로이드에서 인증키 입력시 server에서 send, recv 쓰레드 생성
recv쓰레드에서는 안드로이드에서 입력한 인증키를 받음
send쓰레드에서는 안드로이드에서 입력한 인증키가 있는지 확인하고 있다면 전화번호를 전송해줌
없다면 no 전송

Test_Set_Parent(android) :
인증키 생성과 인증키 입력 구현

인증키 생성은 추후에 멀티 스레드로 바꿔야함
버튼을 입력하면 난수와 함께 전화번호를 서버로 전송
-> 1분이 지나거나 다른 휴대전화와 연결되면 toast msg 출력

인증키 입력은 인증키를 입력하고 버튼을 누르면
결과가 출력됨