async function signup() {

    const name =
        document.getElementById("name").value;

    const email =
        document.getElementById("email").value;

    const password =
        document.getElementById("password").value;

    if(!name || !email || !password){
        alert("Fill all fields");
        return;
    }

    try {

        const response =
            await fetch("/api/signup", {

                method: "POST",

                headers: {
                    "Content-Type":"application/json"
                },

                body: JSON.stringify({
                    name,
                    email,
                    password
                })
            });

        const result =
            await response.text();

        alert(result);

        window.location.href =
            "login.html";

    }
    catch(error){

        console.log(error);

        alert("Signup Failed");
    }
}