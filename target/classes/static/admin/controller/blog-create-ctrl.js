// Khởi tạo Quill
let quillDescription, quillFull;

document.addEventListener('DOMContentLoaded', function () {
    // Khởi tạo Quill cho mô tả
    quillDescription = new Quill('#snow2', {
        theme: 'snow', // Chọn theme snow
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'], // Định dạng văn bản
                [{'list': 'ordered'}, {'list': 'bullet'}], // Danh sách
                ['link', 'image'] // Chèn liên kết và hình ảnh
            ]
        }
    });

    // Khởi tạo Quill cho bài viết chi tiết
    quillFull = new Quill('#full2', {
        theme: 'snow', // Chọn theme snow
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'], // Định dạng văn bản
                [{'list': 'ordered'}, {'list': 'bullet'}], // Danh sách
                ['link', 'image'] // Chèn liên kết và hình ảnh
            ]
        }
    });
});

function createBlog() {
    const titleInput = document.getElementById('title');
    if (!titleInput) {
        console.error('Không tìm thấy phần tử tiêu đề.');
        return;
    }

    const title = titleInput.value.trim();
    const description = quillDescription.root.innerHTML; // Lấy nội dung mô tả dưới dạng HTML
    const blogDetail = quillFull.root.innerHTML; // Lấy nội dung chi tiết dưới dạng HTML
    const imageFileInput = document.getElementById('imageFile'); // Nhận input hình ảnh

    // Kiểm tra các trường cần thiết
    if (!title) {
        showAlert("Vui lòng điền tiêu đề!", 'alert-light-danger');
        return;
    }

    // Tạo FormData để gửi dữ liệu
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description); // Lưu nội dung mô tả HTML
    formData.append('blogDetail', blogDetail); // Lưu nội dung chi tiết HTML

    // Nếu có tệp hình ảnh, thêm vào FormData
    if (imageFileInput && imageFileInput.files.length > 0) {
        formData.append('imageFile', imageFileInput.files[0]);
    }

    // Gửi dữ liệu đến máy chủ
    fetch('/admin/blog-ctrl', {
        method: 'POST',
        body: formData // Không cần thiết lập Content-Type, trình duyệt sẽ tự động thiết lập
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Tạo bài viết thất bại');
                });
            }
            $('#createBlogModal').modal('hide'); // Đóng modal
            showAlert("Bài viết đã được tạo thành công!", 'alert-light-success');
            setTimeout(() => {
                window.location.href = '/admin/blog'; // Thay đổi đường dẫn đến trang danh sách blog của bạn
            }, 1000);
            quillDescription.setContents([]); // Làm sạch mô tả
            quillFull.setContents([]); // Làm sạch bài viết chi tiết
            imageFileInput.value = ''; // Làm sạch input hình ảnh
            previewImage({target: {files: []}}); // Ẩn hình ảnh xem trước
        })
        .catch(error => {
            console.error("Đã xảy ra lỗi:", error.message);
            showAlert(error.message, 'alert-light-danger');
        });
}

function previewImage(event) {
    const imagePreview = document.querySelector('.image-preview');
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function (e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = 'block';
        };

        reader.readAsDataURL(file);
    } else {
        imagePreview.style.display = 'none';
    }
}