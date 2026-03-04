# 1. Giới thiệu chung
Ứng dụng Chat Realtime, có giao diện dễ sử dụng, là ứng dụng web có các chức năng chính : 
<ul>
  <li>
    Đăng ký tài khoản mới.
  </li>
  <li>
    Đăng nhập bằng tài khoản đã có.
  </li>
	<li>
    Tìm kiếm người dùng khác bằng tên.
  </li>
  <li>
    Tạo nhóm chat mới, thêm bạn bè vào nhóm.
  </li>
  <li>
    Chat 1-1 và chat nhóm: Giao diện riêng biệt cho từng cuộc hội thoại.
  </li>
  <li>
    Giao tiếp thời gian thực: Tin nhắn mới xuất hiện ngay lập tức trên màn hình của người nhận mà không cần tải lại trang.
  </li>
  <li>
    Hỗ trợ tin nhắn văn bản, hình ảnh và file: Cho phép người dùng soạn và gửi tin nhắn văn bản, cũng như tải lên và gửi file.
  </li>
  <li>
    Lịch sử trò chuyện: Tải và hiển thị tin nhắn cũ khi người dùng cuộn lên.
  </li>
</ul>

# 2. Thuật toán xử lí chính tại phía Backend
<p>Thuật toán lõi của ứng dụng chính là cơ chế gửi và nhận tin nhắn dựa trên giao thức WebSocket được hỗ trợ bởi Spring Boot. Khác với HTTP, khi mà Client gửi Request lên Server thì khi đó, Server mới trả lời, đáp ứng yêu cầu của Client. WebSocket thiết lập một kênh truyền dẫn 2 chiều trên một kết nối, từ đó Client và Server có thể chủ động gửi dữ liệu cho nhau mà không cần chờ Request.</p>

# 3. Một số hình ảnh giao diện web

### Giao diện đăng nhập:
<img width="533" height="370" alt="Picture1" src="https://github.com/user-attachments/assets/fa17c88c-f7db-4920-853e-850c2655edd3" />

### Giao diện đăng ký:
<img width="548" height="506" alt="Picture2" src="https://github.com/user-attachments/assets/aa092e77-79c7-4a17-b42b-7b578a02fe07" />

### Giao diện tìm kiếm bạn bè: 
<img width="563" height="478" alt="Picture3" src="https://github.com/user-attachments/assets/e112503c-20c7-4b16-a241-639579bc6765" />

### Giao diện tùy chỉnh thông tin cá nhân:
<img width="575" height="445" alt="Picture4" src="https://github.com/user-attachments/assets/731ddd22-1752-4353-8b69-aa8d02243492" />

### Giao diện chat:
<img width="568" height="432" alt="Picture5" src="https://github.com/user-attachments/assets/accf9a30-3734-4c5f-92d3-1ed69db9d40a" />

