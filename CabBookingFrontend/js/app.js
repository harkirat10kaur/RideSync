function goToLogin() {
    window.location.href = "login.html";
}

function login() {

    let email =
        document.getElementById("email").value.trim();

    let password =
        document.getElementById("password").value.trim();

    if(email === "" || password === "") {

        alert("Please fill all fields");
        return;
    }

    fetch("/api/login", {

        method: "POST",

        headers: {
            "Content-Type":"application/json"
        },

        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(response => response.json())
    .then(data => {

        if(data.success){

            localStorage.setItem(
                "userName",
                data.name
            );

            window.location.href =
                "booking.html";
        }
        else{
            alert("Invalid Email or Password");
        }
    })
    .catch(error => {

        console.log(error);

        alert("Login Failed");
    });
}