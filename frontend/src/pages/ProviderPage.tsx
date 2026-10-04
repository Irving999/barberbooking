import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";

type Slot = {
  slotId: number;
  barberName: string;
  serviceName: string;
  slotDate: string;
  startTime: string;
  endTime: string;
};

type ProviderAppointment = {
  appointmentId: number;
  slotId: number;
  customerName: string;
  customerEmail: string;
  serviceName: string;
  slotDate: string;
  startTime: string;
  endTime: string;
  bookedAt: string;
  appointmentStatus: string;
};

export default function ProviderPage() {
  const [serviceId, setServiceId] = useState("1");
  const [slotDate, setSlotDate] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  const [slots, setSlots] = useState<Slot[]>([]);
  const [appointments, setAppointments] =
    useState<ProviderAppointment[]>([]);

  const [message, setMessage] = useState("");

  useEffect(() => {
    loadSlots();
    loadAppointments();
  }, []);

  async function createSlot() {
    setMessage("");

    if (!slotDate || !startTime || !endTime) {
      setMessage("Please complete all fields.");
      return;
    }

    try {
      const response = await fetch("/api/provider/slots", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          serviceId: Number(serviceId),
          slotDate,
          startTime,
          endTime,
        }),
      });

      if (response.status === 401 || response.status === 403) {
        setMessage("You must be logged in as a provider.");
        return;
      }

      if (!response.ok) {
        let errorMessage = "Unable to create availability.";

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

      setMessage("Availability created.");

      setSlotDate("");
      setStartTime("");
      setEndTime("");

      await loadSlots();
    } catch {
      setMessage("Unable to connect to the server.");
    }
  }

  async function loadSlots() {
    try {
      const response = await fetch("/api/provider/slots", {
        credentials: "include",
      });

      if (!response.ok) {
        setSlots([]);
        return;
      }

      const data: Slot[] = await response.json();
      setSlots(data);
    } catch {
      setSlots([]);
    }
  }

  async function removeSlot(slotId: number) {
    const confirmed = window.confirm(
      "Are you sure you want to remove this slot?"
    );

    if (!confirmed) {
      return;
    }

    setMessage("");

    try {
      const response = await fetch(
        `/api/provider/slots/${slotId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (response.status === 409) {
        setMessage("A booked slot cannot be removed.");
        return;
      }

      if (response.status === 404) {
        setMessage("Slot not found.");
        return;
      }

      if (!response.ok) {
        setMessage("Unable to remove availability.");
        return;
      }

      setMessage("Availability removed.");

      await loadSlots();
    } catch {
      setMessage("Unable to connect to the server.");
    }
  }

  async function loadAppointments() {
    try {
      const response = await fetch(
        "/api/appointments/provider",
        {
          credentials: "include",
        }
      );

      if (!response.ok) {
        setAppointments([]);
        return;
      }

      const data: ProviderAppointment[] =
        await response.json();

      setAppointments(data);
    } catch {
      setAppointments([]);
    }
  }

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content">
        <div className="page-title">
          <h1>Provider Dashboard</h1>
          <p>
            Manage your availability and view your appointments.
          </p>
        </div>

        <div className="message-box">
          {message}
        </div>

        <section className="provider-section">
          <h2>Create Availability</h2>

          <div className="form-group">
            <label htmlFor="provider-service">
              Service
            </label>

            <select
              id="provider-service"
              className="form-control"
              value={serviceId}
              onChange={(event) =>
                setServiceId(event.target.value)
              }
            >
              <option value="1">Haircut</option>
              <option value="2">Beard Trim</option>
              <option value="3">
                Haircut + Beard
              </option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="slot-date">
              Date
            </label>

            <input
              id="slot-date"
              className="form-control"
              type="date"
              value={slotDate}
              onChange={(event) =>
                setSlotDate(event.target.value)
              }
            />
          </div>

          <div className="form-group">
            <label htmlFor="start-time">
              Start Time
            </label>

            <input
              id="start-time"
              className="form-control"
              type="time"
              value={startTime}
              onChange={(event) =>
                setStartTime(event.target.value)
              }
            />
          </div>

          <div className="form-group">
            <label htmlFor="end-time">
              End Time
            </label>

            <input
              id="end-time"
              className="form-control"
              type="time"
              value={endTime}
              onChange={(event) =>
                setEndTime(event.target.value)
              }
            />
          </div>

          <button
            className="primary-button"
            type="button"
            onClick={createSlot}
          >
            Create Availability
          </button>
        </section>

        <section className="provider-section">
          <h2>My Availability</h2>

          {slots.length === 0 && (
            <p className="empty-message">
              No available slots.
            </p>
          )}

          {slots.map((slot) => (
            <div
              key={slot.slotId}
              className="provider-row"
            >
              <div>
                <strong>
                  {slot.serviceName}
                </strong>

                <div className="slot-meta">
                  {slot.slotDate}
                  {" · "}
                  {formatTime(slot.startTime)}
                  {" - "}
                  {formatTime(slot.endTime)}
                </div>
              </div>

              <button
                className="remove-slot-button"
                type="button"
                onClick={() =>
                  removeSlot(slot.slotId)
                }
              >
                Remove
              </button>
            </div>
          ))}
        </section>

        <section className="provider-section">
          <h2>Booked Appointments</h2>

          {appointments.length === 0 && (
            <p className="empty-message">
              No appointments.
            </p>
          )}

          {appointments.map((appointment) => (
            <div
              key={appointment.appointmentId}
              className="appointment-card"
            >
              <div className="appointment-card-header">
                <div>
                  <strong>
                    {appointment.customerName}
                  </strong>

                  <div className="appointment-date">
                    {appointment.customerEmail}
                  </div>
                </div>

                <span className="status-badge">
                  {appointment.appointmentStatus}
                </span>
              </div>

              <div className="appointment-card-body">
                <div className="appointment-detail">
                  <span>Service</span>

                  <strong>
                    {appointment.serviceName}
                  </strong>
                </div>

                <div className="appointment-detail">
                  <span>Date</span>

                  <strong>
                    {appointment.slotDate}
                  </strong>
                </div>

                <div className="appointment-detail">
                  <span>Time</span>

                  <strong>
                    {formatTime(appointment.startTime)}
                    {" - "}
                    {formatTime(appointment.endTime)}
                  </strong>
                </div>
              </div>
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

  const suffix =
    hour >= 12 ? "PM" : "AM";

  hour = hour % 12;

  if (hour === 0) {
    hour = 12;
  }

  return `${hour}:${minute} ${suffix}`;
}