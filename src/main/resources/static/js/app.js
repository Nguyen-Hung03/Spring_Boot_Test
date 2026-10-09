$(function () {
    $("#userForm").on("submit", function (event) {
        var errors = [];
        var name = $.trim($("#name").val());
        var age = $.trim($("#age").val());
        var salary = $.trim($("#salary").val());
        var ageNumber = parseInt(age, 10);
        var salaryNumber = Number(salary);

        if (!name) {
            errors.push("Name is required");
        } else if (!/^[\p{L} ]+$/u.test(name)) {
            errors.push("Name must contain only letters and spaces");
        } else if (name.length < 2 || name.length > 50) {
            errors.push("Name must be between 2 and 50 characters");
        }

        if (!age) {
            errors.push("Age is required");
        } else if (!/^\d+$/.test(age) || ageNumber < 1 || ageNumber > 120) {
            errors.push("Age must be a number from 1 to 120");
        }

        if (!salary) {
            errors.push("Salary is required");
        } else if (!/^\d+$/.test(salary) || salaryNumber <= 0) {
            errors.push("Salary must be greater than 0");
        }

        var $clientError = $("#clientError");
        if (errors.length) {
            event.preventDefault();
            $clientError.removeClass("d-none").html(errors.join("<br>"));
            return;
        }
        $clientError.addClass("d-none").empty();
    });

    $(document).on("click", ".btn-remove", function (event) {
        var name = $(this).closest("tr").children("td").eq(1).text();
        if (!window.confirm("Remove user " + name + "?")) {
            event.preventDefault();
        }
    });
});
