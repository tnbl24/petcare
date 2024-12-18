let quillSnow;
let quillFull;

document.addEventListener('DOMContentLoaded', function() {
    const blogId = localStorage.getItem('editingBlogId');
    if (blogId) {
        fetchBlogById(blogId);
    }

    // Khởi tạo Quill
    quillSnow = new Quill('#snow2', {
        theme: 'snow'
    });

    quillFull = new Quill('#full2', {
        theme: 'snow'
    });
});

// Hàm xử lý sự kiện cập nhật
function handleUpdate() {
    const blogId = localStorage.getItem('editingBlogId'); // Lấy blogId từ localStorage
    updateBlog(blogId);
}

function fetchBlogById(blogId) {
    fetch(`/admin/blog-ctrl/${blogId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể lấy bài viết');
            }
            return response.json();
        })
        .then(blog => {
            console.log('Dữ liệu blog:', blog); // Kiểm tra dữ liệu nhận được
            document.querySelector('input[name="name"]').value = blog.title || ''; // Cập nhật tiêu đề
            quillSnow.root.innerHTML = blog.description || ''; // Cập nhật mô tả
            quillFull.clipboard.dangerouslyPasteHTML(blog.blogDetail || ''); // Cập nhật nội dung chi tiết
            if (blog.image) {
                document.querySelector('.image-preview').src = `data:image/jpeg;base64,${blog.image}`;
                document.querySelector('.image-preview').style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy dữ liệu bài viết:', error);
        });
}
function updateBlog(blogId) {
    const formData = new FormData();

    // Lấy giá trị tiêu đề từ input
    const title = document.querySelector('input[name="name"]').value;
    if (!title) {
        console.error('Tiêu đề không hợp lệ');
        return;
    }
    formData.append('title', title); // Gửi tiêu đề (thay đổi từ 'name' sang 'title')

    // Gửi mô tả và nội dung
    const description = quillSnow.root.innerHTML; // Mô tả
    const blogDetail = quillFull.root.innerHTML; // Nội dung chi tiết
    formData.append('description', description); // Gửi mô tả
    formData.append('blogDetail', blogDetail); // Gửi nội dung chi tiết (thay đổi từ 'content' sang 'blogDetail')

    // Xử lý hình ảnh
    const imageFile = document.querySelector('input[name="imageFile"]').files[0];
    if (imageFile) {
        formData.append('imageFile', imageFile); // Gửi hình ảnh nếu có
    }

    // Gửi dữ liệu lên API
    fetch(`/admin/blog-ctrl/${blogId}`, {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể cập nhật bài viết');
            }
            return response.json();
        })
        .then(updatedBlog => {
            console.log('Bài viết đã được cập nhật:', updatedBlog);
            showAlert("Bài viết đã được cập nhật thành công!", 'alert-light-success');
            setTimeout(() => {
                window.location.href = '/admin/blog'; // Thay đổi đường dẫn đến trang danh sách blog của bạn
            }, 1000);
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

        reader.onload = function(e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = 'block';
        };

        reader.readAsDataURL(file);
    } else {
        imagePreview.style.display = 'none';
    }
}