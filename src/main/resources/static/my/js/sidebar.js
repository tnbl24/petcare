document.addEventListener("DOMContentLoaded", function () {
    // Lấy đường dẫn hiện tại
    const currentPath = window.location.pathname;

    // Lấy tất cả các liên kết trong sidebar
    const sidebarLinks = document.querySelectorAll('.sidebar-link');

    sidebarLinks.forEach(link => {
        const linkPath = link.getAttribute('href');

        if (currentPath === linkPath || (linkPath === "/admin" && currentPath === "/admin/")) {
            link.closest('.sidebar-item').classList.add('active');
        }
    });


});

document.addEventListener("DOMContentLoaded", function () {
    // Lấy URL hiện tại
    const currentUrl = window.location.pathname;

    // Lấy tất cả các nav-link
    const navLinks = document.querySelectorAll('.nav-item.nav-link');

    navLinks.forEach(link => {
        // Lấy href của từng liên kết
        const href = link.getAttribute('href');

        // Kiểm tra nếu href trùng với URL hiện tại
        if (href === currentUrl) {
            link.classList.add('active'); // Thêm lớp active
        } else {
            link.classList.remove('active'); // Xóa lớp active
        }
    });
});


$(document).ready(function(){
    // Khởi tạo datepicker cho tất cả các input có class 'datepicker'
    $('.datepicker').datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    });
});

function showAlert(message,color) {
    const alertDiv = document.getElementById('alert');
    alertDiv.textContent = message; // Cập nhật nội dung thông báo
    alertDiv.classList.add(color)
    alertDiv.style.display = 'block'; // Hiển thị alert
    alertDiv.classList.add('show'); // Thêm lớp show để hiển thị hiệu ứng

    // Tự động ẩn alert sau 2 giây
    setTimeout(() => {
        alertDiv.classList.remove('show'); // Loại bỏ lớp show
        alertDiv.style.display = 'none'; // Ẩn alert
    }, 2000);
}
