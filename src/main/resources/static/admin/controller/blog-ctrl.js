document.addEventListener("DOMContentLoaded", function () {
    const tableBody = document.getElementById('BlogBody');
    const searchInput = document.querySelector('.dataTable-input');
    let searchTerm = '';
    let currentPage = 1;

    function fetchBlogs(page, search = '') {
        console.log(`Đang lấy bài viết cho trang: ${page} với từ khóa tìm kiếm: "${search}"`);

        const searchRequestDTO = {
            searchRequestDTO: [
                {
                    column: "title",
                    value: search,
                    operation: "LIKE",
                },

                {
                    column: "description",
                    value: search,
                    operation: "LIKE",
                }
            ],
            globalOperator: "OR"
        };

        fetch(`/admin/blog-ctrl/filter/page/${page - 1}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(searchRequestDTO)
        })
            .then(response => {
                if (!response.ok) {
                    console.error('Phản hồi mạng không hợp lệ:', response.statusText);
                    throw new Error('Phản hồi mạng không hợp lệ');
                }
                return response.json();
            })
            .then(data => {
                console.log('Dữ liệu nhận được:', data);
                tableBody.innerHTML = '';

                data.content.forEach((blog, index) => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td class="text-center">${(page - 1) * 10 + (index + 1)}</td>
                    <td>${blog.title || ''}</td>
                    <td class="text-center">${blog.image ? `<img src="data:image/jpeg;base64,${blog.image}" alt="Hình ảnh" style="width: 100px; height: 100px;">` : ''}</td>
                    <td>${blog.description || ''}</td>
                    <td class="text-center">
                        <a href="#" title="Chỉnh sửa" onclick="editBlog(${blog.id})">
                <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg" style="margin-right: 5px"></i>
            </a>
                        <a href="#" title="Xóa" style="margin-left: 10px;" onclick="openDeleteModal(${blog.id}, '${blog.title}')">
                            <i class="fas fa-trash badge-circle badge-circle-light-secondary font-medium-1 delete-icon fa-lg"></i>
                        </a>
                    </td>
                `;
                    tableBody.appendChild(row);
                });

                updatePagination(data.totalPages, page);
            })
            .catch(error => {
                console.error('Lỗi khi lấy bài viết:', error);
            });
    }

    function loadBlogs() {
        fetchBlogs(currentPage, searchTerm);
    }

    searchInput.addEventListener('input', debounce(function () {
        searchTerm = this.value.trim();
        currentPage = 1; // Đặt lại về trang đầu khi tìm kiếm mới
        loadBlogs(); // Tải bài viết với từ khóa tìm kiếm
    }, 300)); // Thời gian chờ debounce là 300ms

    function debounce(func, delay) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    function updatePagination(totalPages, currentPage) {
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';

        const prevPage = document.createElement('li');
        prevPage.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
        prevPage.innerHTML = `<a href="#" class="page-link" data-page="${currentPage - 1}">‹</a>`;
        prevPage.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage > 1) {
                fetchBlogs(currentPage - 1, searchTerm);
            }
        });
        pagination.appendChild(prevPage);

        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            const pageItem = document.createElement('li');
            pageItem.className = `page-item ${currentPage === i ? 'active' : ''}`;
            pageItem.innerHTML = `<a href="#" class="page-link" data-page="${i}">${i}</a>`;
            pageItem.addEventListener('click', (e) => {
                e.preventDefault();
                fetchBlogs(i, searchTerm);
            });
            pagination.appendChild(pageItem);
        }

        const nextPage = document.createElement('li');
        nextPage.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextPage.innerHTML = `<a href="#" class="page-link" data-page="${currentPage + 1}">›</a>`;
        nextPage.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage < totalPages) {
                fetchBlogs(currentPage + 1, searchTerm);
            }
        });
        pagination.appendChild(nextPage);
    }

    loadBlogs(); // Gọi hàm để tải bài viết ban đầu

    window.openDeleteModal = function(blogId, blogTitle) {
        blogIdToDelete = blogId;
        document.getElementById('BlogToDelete').innerText = blogTitle;
        $('#delete-service-detail').modal('show');
    };

    window.deleteServiceDetail = function() {
        if (blogIdToDelete) {
            fetch(`/admin/blog-ctrl/${blogIdToDelete}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            })
                .then(response => response.text())
                .then(data => {
                    console.log('Phản hồi từ máy chủ:', data);
                    showAlert(data, 'alert-light-success');
                    loadBlogs();
                    $('#delete-service-detail').modal('hide');
                })
                .catch(error => {
                    console.error('Lỗi khi xóa bài viết:', error);
                    showAlert('Xóa dịch vụ thất bại!', 'alert-light-danger');
                });
        }
    };
});
function editBlog(blogId) {
    // Lưu ID vào local storage
    localStorage.setItem('editingBlogId', blogId);
    // Chuyển hướng đến trang chỉnh sửa
    window.location.href = 'http://localhost:8080/admin/blog-edit';
}