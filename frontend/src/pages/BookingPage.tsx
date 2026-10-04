import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

type Slot = {
  slotId: number;
  barberName: string;
  serviceName: string;
  slotDate: string;
  startTime: string;
  endTime: string;
};

export default function BookingPage() {
  const navigate = useNavigate();

  const [slot, setSlot] = useState<Slot | null>(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const savedSlot = sessionStorage.getItem("selectedSlot");

    if (!savedSlot) {
      setMessage("No appointment slot was selected.");
      return;
    }

    setSlot(JSON.parse(savedSlot));
  }, []);

  async function confirmBooking() {
    if (!slot) {
      return;
    }

    setMessage("");
    setLoading(true);

    try {
      const response = await fetch("/api/appointments", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          slotId: slot.slotId,
        }),
      });

      if (response.status === 401 || response.status === 403) {
        setMessage(
          "You must be logged in as a customer to book an appointment."
        );
        return;
      }

      if (response.status === 409) {
        setMessage(
          "That slot is no longer available. Please choose another time."
        );
        return;
      }

      if (!response.ok) {
        let errorMessage = "Unable to book appointment.";

        try {
          const error = await response.json();

          if (error.error) {
            errorMessage = error.error;
          }
        } catch {
          // Keep default message.
        }

        setMessage(errorMessage);
        return;
      }

      sessionStorage.setItem(
        "confirmedAppointment",
        JSON.stringify(slot)
      );

      sessionStorage.removeItem("selectedSlot");

      navigate("/confirmation");
    } catch {
      setMessage("Unable to connect to the server.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content">
        <div className="page-title">
          <h1>Book Appointment</h1>
          <p>
            Review your selected appointment before confirming.
          </p>
        </div>

        {slot && (
          <div className="appointment-summary">
            <div className="summary-row">
              <span>Barber</span>
              <strong>{slot.barberName}</strong>
            </div>

            <div className="summary-row">
              <span>Service</span>
              <strong>{slot.serviceName}</strong>
            </div>

            <div className="summary-row">
              <span>Date</span>
              <strong>{slot.slotDate}</strong>
            </div>

            <div className="summary-row">
              <span>Time</span>
              <strong>
                {formatTime(slot.startTime)}
                {" - "}
                {formatTime(slot.endTime)}
              </strong>
            </div>
          </div>
        )}

        <div className="message-box">
          {message}
        </div>

        <button
          className="primary-button"
          type="button"
          onClick={confirmBooking}
          disabled={!slot || loading}
        >
          {loading ? "Booking..." : "Confirm Booking"}
        </button>

        <button
          className="secondary-button"
          type="button"
          onClick={() => navigate("/slots")}
        >
          Back to Available Slots
        </button>
      </main>
    </div>
  );
}

function formatTime(time: string) {
  const [hourString, minute] = time.split(":");

  let hour = Number(hourString);

  const suffix = hour >= 12 ? "PM" : "AM";

  hour = hour % 12;

  if (hour === 0) {
    hour = 12;
  }

  return `${hour}:${minute} ${suffix}`;
}