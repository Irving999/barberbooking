import { useState } from "react";
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

export default function SlotsPage() {
  const navigate = useNavigate();

  const [serviceId, setServiceId] = useState("");
  const [providerId, setProviderId] = useState("");
  const [date, setDate] = useState("");

  const [slots, setSlots] = useState<Slot[]>([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function searchSlots() {
    setMessage("");
    setLoading(true);

    const params = new URLSearchParams();

    if (serviceId) {
      params.append("serviceId", serviceId);
    }

    if (providerId) {
      params.append("providerId", providerId);
    }

    if (date) {
      params.append("date", date);
    }

    params.append("page", "0");
    params.append("size", "20");

    try {
      const response = await fetch(
        `/api/slots?${params.toString()}`,
        {
          credentials: "include",
        }
      );

      if (!response.ok) {
        setMessage("Unable to load available slots.");
        setSlots([]);
        return;
      }

      const data: Slot[] = await response.json();

      setSlots(data);

      if (data.length === 0) {
        setMessage("No available slots found.");
      }
    } catch {
      setMessage("Unable to connect to the server.");
      setSlots([]);
    } finally {
      setLoading(false);
    }
  }

  function selectSlot(slot: Slot) {
    sessionStorage.setItem(
      "selectedSlot",
      JSON.stringify(slot)
    );

    navigate("/booking");
  }

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content">
        <div className="page-title">
          <h1>View Available Slots</h1>
          <p>
            Choose a service, barber, and date to find an available time.
          </p>
        </div>

        <div className="form-group">
          <label htmlFor="service">Service</label>

          <select
            id="service"
            className="form-control"
            value={serviceId}
            onChange={(event) =>
              setServiceId(event.target.value)
            }
          >
            <option value="">All Services</option>
            <option value="1">Haircut</option>
            <option value="2">Beard Trim</option>
            <option value="3">Haircut + Beard</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="provider">Barber</label>

          <select
            id="provider"
            className="form-control"
            value={providerId}
            onChange={(event) =>
              setProviderId(event.target.value)
            }
          >
            <option value="">All Barbers</option>
            <option value="1">Alex</option>
            <option value="2">Marcus</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="date">Date</label>

          <input
            id="date"
            className="form-control"
            type="date"
            value={date}
            onChange={(event) =>
              setDate(event.target.value)
            }
          />
        </div>

        <button
          className="primary-button"
          type="button"
          onClick={searchSlots}
          disabled={loading}
        >
          {loading
            ? "Searching..."
            : "Search Available Slots"}
        </button>

        <div className="message-box">
          {message}
        </div>

        <section className="slots-section">
          <h2>Available Times</h2>

          {slots.map((slot) => (
            <div
              key={slot.slotId}
              className="slot-row"
            >
              <div>
                <strong>
                  {formatTime(slot.startTime)}
                </strong>

                <div className="slot-meta">
                  {slot.barberName}
                  {" · "}
                  {slot.serviceName}
                  {" · "}
                  {slot.slotDate}
                </div>
              </div>

              <button
                type="button"
                className="slot-book-button"
                onClick={() => selectSlot(slot)}
              >
                Book
              </button>
            </div>
          ))}
        </section>
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