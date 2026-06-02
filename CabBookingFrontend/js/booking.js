let currentFare = 0;
let currentDistance = 0;
let map;
let routeLayer;

const userName = localStorage.getItem("userName");

window.onload = function () {

    map = L.map("map").setView(
        [30.7333, 76.7794],
        10
    );

    L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
            attribution:
                "&copy; OpenStreetMap contributors"
        }
    ).addTo(map);

    loadHistory();
};

document.getElementById("welcomeUser").innerText =
    "Welcome, " + userName;

async function getCoordinates(place) {

    const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(place)}`
    );

    const data = await response.json();

    if(data.length === 0) {
        throw new Error("Location not found");
    }

    return {
        lat: data[0].lat,
        lon: data[0].lon
    };
}

async function calculateDistance() {

    const pickup =
        document.getElementById("pickup").value;

    const destination =
        document.getElementById("destination").value;

    if(!pickup || !destination) {

        alert("Enter pickup and destination");

        return;
    }

    try {

        const start =
            await getCoordinates(pickup);

        const end =
            await getCoordinates(destination);

        const response =
            await fetch(
                `https://router.project-osrm.org/route/v1/driving/${start.lon},${start.lat};${end.lon},${end.lat}?overview=false`
            );

        const data =
            await response.json();

        currentDistance =
            data.routes[0].distance / 1000;

        document.getElementById("distanceText")
            .innerText =
            currentDistance.toFixed(2) + " KM";

    }

    catch(err) {

        alert("Location not found");

    }

}

async function calculateFare() {

    const pickup =
        document.getElementById("pickup").value.trim();

    const destination =
        document.getElementById("destination").value.trim();

    const vehicle =
        document.getElementById("vehicle").value;

    if(!pickup || !destination) {

        alert("Enter pickup and destination");

        return;
    }

    try {

        const start =
            await getCoordinates(pickup);

        const end =
            await getCoordinates(destination);
            await drawRoute(start, end);

        const routeResponse =
            await fetch(
                `https://router.project-osrm.org/route/v1/driving/${start.lon},${start.lat};${end.lon},${end.lat}?overview=false`
            );

        const routeData =
            await routeResponse.json();

        currentDistance =
            routeData.routes[0].distance / 1000;

        document.getElementById("distanceText")
            .innerText =
            currentDistance.toFixed(2) + " KM";

        const fareResponse =
            await fetch("/api/calculate-fare", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    vehicle: vehicle,
                    distance: currentDistance

                })

            });

        const fareData =
            await fareResponse.json();

        currentFare =
            fareData.fare;

        document.getElementById("fareAmount")
            .innerText =
            "₹" + currentFare;

        document.getElementById("bookBtn")
            .style.display =
            "block";

    }

    catch(err) {

        console.log(err);

        alert(
            "Location not found. Use full names like Chandigarh or Ludhiana."
        );

    }

}

async function bookRide() {

    const pickup =
        document.getElementById("pickup").value.trim();

    const destination =
        document.getElementById("destination").value.trim();

    const vehicle =
        document.getElementById("vehicle").value;

    if (!pickup || !destination) {

        alert("Enter pickup and destination");
        return;

    }

    try {

        const response = await fetch("/api/book", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                userName: userName,
                pickup: pickup,
                destination: destination,
                vehicle: vehicle,
                distance: currentDistance,
                fare: currentFare

            })

        });

        const result = await response.json();

        if (result.success) {

            alert("Ride Booked Successfully 🚖");
            startRideTracking();

            document.getElementById("pickup").value = "";
            document.getElementById("destination").value = "";

            document.getElementById("distanceText").innerText =
                "0 KM";

            document.getElementById("fareAmount").innerText =
                "₹0";

            currentDistance = 0;
            currentFare = 0;

            document.getElementById("bookBtn").style.display =
                "none";

            loadHistory();

        } else {

            alert(result.error || "Booking Failed");

        }

    } catch (err) {

        console.error(err);

        alert("Booking Failed");

    }
}

async function loadHistory() {

    try {

        const response =
            await fetch(
                "/api/history?name=" +
                encodeURIComponent(userName)
            );

        const bookings =
            await response.json();

        let html = "";

        bookings.forEach(booking => {

            html += `
                <tr>
                    <td>${booking.bookingId || "-"}</td>
                    <td>${booking.bookingTime || "-"}</td>
                    <td>${booking.vehicle || "-"}</td>
                    <td>${booking.driverName || "-"}</td>
                    <td>${booking.cabNumber || "-"}</td>
                    <td>₹${booking.fare}</td>
                    <td>${booking.status || "-"}</td>

                    <td>
                        ${
                            booking.status === "Confirmed"
                            ?
                            `<button class="cancel-btn"
                                onclick="cancelBooking('${booking.bookingId}')">
                                Cancel
                             </button>`
                            :
                            "-"
                        }
                    </td>
                </tr>
            `;

        });

        document.getElementById("historyTable").innerHTML = html;

    }

    catch(err) {

        console.log(err);

    }
}

function logout() {

    localStorage.clear();

    window.location.href =
        "login.html";

}

loadHistory();

async function cancelBooking(bookingId) {

    if(!confirm("Cancel this booking?")) {
        return;
    }

    try {

        const response =
            await fetch("/api/cancelBooking", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    bookingId: bookingId
                })
            });

        const result = await response.text();

        alert(result);

        loadHistory();

    }

    catch(error) {

        console.error(error);

        alert("Cancellation failed");
    }
}

async function drawRoute(start, end) {

    const response =
        await fetch(
            `https://router.project-osrm.org/route/v1/driving/${start.lon},${start.lat};${end.lon},${end.lat}?overview=full&geometries=geojson`
        );

    const data =
        await response.json();

    const route =
        data.routes[0].geometry;

    if(routeLayer) {
        map.removeLayer(routeLayer);
    }

    routeLayer =
        L.geoJSON(route).addTo(map);

    map.fitBounds(
        routeLayer.getBounds()
    );

    L.marker([
        start.lat,
        start.lon
    ]).addTo(map);

    L.marker([
        end.lat,
        end.lon
    ]).addTo(map);
}

function startRideTracking() {

    const updates = [
    "Driver assigned 🚖",
    "Driver is on the way",
    "Driver reached pickup point",
    "Ride started",
    "Ride completed"
    ];

    let index = 0;

    const interval = setInterval(() => {

    document.getElementById("rideStatus").innerHTML =
    updates[index];

    index++;

    if(index >= updates.length){
    clearInterval(interval);
    }

    }, 5000);
    }