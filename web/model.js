'use strict';

/**
 * Domain model for the Clinic Appointment System web demo.
 *
 * The classes mirror the Java solution: HealthProfessional is the abstract
 * base type, the four subclasses add their own details, and AppointmentManager
 * owns the collection of appointments. The file has no DOM dependencies so it
 * can be tested on its own.
 */

const CLINIC = Object.freeze({
  name: 'Clinic Appointment System',
  openingHour: 8,
  closingHour: 16,
  slotMinutes: 30,
});

const TYPE_SHORT = Object.freeze({
  gp: 'GP',
  nurse: 'Nurse',
  surgeon: 'Surgeon',
  dietitian: 'Dietitian',
});

const WEEKDAYS = Object.freeze([
  'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday',
]);

/** Pads a number with a leading zero when needed. */
function pad2(value) {
  return String(value).padStart(2, '0');
}

/**
 * One fixed 30-minute appointment slot in the clinic day.
 */
class TimeSlot {
  constructor(startMinute) {
    this.startMinute = startMinute;
  }

  /**
   * Returns the schedule slot for the given minute of the day, or null when
   * the minute is not part of the clinic schedule.
   * @param {number} minute minutes from midnight
   * @returns {TimeSlot|null}
   */
  static fromMinutes(minute) {
    return TimeSlot.dailySchedule().find((slot) => slot.startMinute === minute) || null;
  }

  /**
   * Parses text such as "09:30". Returns null for invalid or out-of-hours
   * times so the UI can show its own message.
   * @param {string} text
   * @returns {TimeSlot|null}
   */
  static fromText(text) {
    if (typeof text !== 'string') {
      return null;
    }
    const match = text.trim().match(/^(\d{1,2}):(\d{2})$/);
    if (!match) {
      return null;
    }
    const hour = Number(match[1]);
    const minute = Number(match[2]);
    if (hour > 23 || minute > 59 || minute % CLINIC.slotMinutes !== 0) {
      return null;
    }
    return TimeSlot.fromMinutes(hour * 60 + minute);
  }

  /**
   * Builds the fixed daily schedule from the clinic constants. Changing the
   * constants at the top of this file changes the schedule everywhere.
   * @returns {TimeSlot[]}
   */
  static dailySchedule() {
    if (!TimeSlot.schedule) {
      const slots = [];
      const opening = CLINIC.openingHour * 60;
      const closing = CLINIC.closingHour * 60;
      for (let minute = opening; minute < closing; minute += CLINIC.slotMinutes) {
        slots.push(new TimeSlot(minute));
      }
      TimeSlot.schedule = Object.freeze(slots);
    }
    return TimeSlot.schedule;
  }

  get endMinute() {
    return this.startMinute + CLINIC.slotMinutes;
  }

  get label() {
    return `${pad2(Math.floor(this.startMinute / 60))}:${pad2(this.startMinute % 60)}`;
  }

  get endLabel() {
    return `${pad2(Math.floor(this.endMinute / 60))}:${pad2(this.endMinute % 60)}`;
  }

  get rangeLabel() {
    return `${this.label} - ${this.endLabel}`;
  }

  equals(other) {
    return other instanceof TimeSlot && other.startMinute === this.startMinute;
  }
}

/**
 * Common base type for every health professional.
 */
class HealthProfessional {
  constructor(id, name, days) {
    const identifier = String(id).trim();
    if (!/^\d+$/.test(identifier)) {
      throw new Error('Health professional ID must contain digits only.');
    }
    if (!name || !name.trim()) {
      throw new Error('Health professional name must not be empty.');
    }
    if (!Array.isArray(days) || days.length === 0) {
      throw new Error('A health professional must have at least one available day.');
    }
    days.forEach((day) => {
      if (!WEEKDAYS.includes(day)) {
        throw new Error(`Invalid available day: ${day}.`);
      }
    });

    this.id = identifier;
    this.name = name.trim();
    this.days = [...new Set(days)];
  }

  /** @returns {string} */
  get type() {
    throw new Error('Subclasses must provide a professional type.');
  }

  /** @returns {string} */
  get typeKey() {
    throw new Error('Subclasses must provide a type key.');
  }

  /** @returns {string} */
  get detailLabel() {
    return 'Details';
  }

  /** @returns {string} */
  get detail() {
    return '';
  }

  /** @returns {string} */
  get duty() {
    return '';
  }

  /** @returns {string} */
  get typeShort() {
    return TYPE_SHORT[this.typeKey] || this.type;
  }

  /** @returns {string} two initials used by the avatar */
  get initials() {
    const withoutTitle = this.name.replace(/^(Dr|Ms|Mr|Mrs|Prof|Nurse)\.?\s+/i, '');
    return withoutTitle
      .split(/\s+/)
      .slice(0, 2)
      .map((part) => part.charAt(0))
      .join('')
      .toUpperCase();
  }

  /** @returns {string} e.g. "Mon - Wed - Fri" */
  get daysShort() {
    return this.days.map((day) => day.slice(0, 3)).join(' - ');
  }

  /** @returns {string} e.g. "Dr Alice Chen (ID 1001)" */
  get summary() {
    return `${this.name} (ID ${this.id})`;
  }

  /** @returns {boolean} */
  worksOn(dayName) {
    return this.days.includes(dayName);
  }
}

class GeneralPractitioner extends HealthProfessional {
  constructor(id, name, days, room) {
    super(id, name, days);
    this.room = room;
  }

  get type() { return 'General Practitioner'; }
  get typeKey() { return 'gp'; }
  get detailLabel() { return 'Consulting room'; }
  get detail() { return this.room; }
  get duty() { return 'Prescribes medication'; }
}

class Nurse extends HealthProfessional {
  constructor(id, name, days, department) {
    super(id, name, days);
    this.department = department;
  }

  get type() { return 'Nurse'; }
  get typeKey() { return 'nurse'; }
  get detailLabel() { return 'Department'; }
  get detail() { return this.department; }
  get duty() { return 'Takes blood samples'; }
}

class Surgeon extends HealthProfessional {
  constructor(id, name, days, specialty) {
    super(id, name, days);
    this.specialty = specialty;
  }

  get type() { return 'Surgeon'; }
  get typeKey() { return 'surgeon'; }
  get detailLabel() { return 'Surgical specialty'; }
  get detail() { return this.specialty; }
  get duty() { return 'Performs surgery'; }
}

class Dietitian extends HealthProfessional {
  constructor(id, name, days, specialisation) {
    super(id, name, days);
    this.specialisation = specialisation;
  }

  get type() { return 'Dietitian'; }
  get typeKey() { return 'dietitian'; }
  get detailLabel() { return 'Specialisation'; }
  get detail() { return this.specialisation; }
  get duty() { return 'Creates meal plans'; }
}

/** A patient record: name plus a normalised Australian mobile number. */
class Patient {
  constructor(name, mobile) {
    if (!name || !name.trim()) {
      throw new Error('Patient name must not be empty.');
    }
    this.name = name.trim();
    this.mobile = Patient.normaliseMobile(mobile);
  }

  /**
   * Removes spaces and hyphens and checks the Australian mobile format.
   * @param {string} mobile
   * @returns {string} ten digits
   */
  static normaliseMobile(mobile) {
    const digits = String(mobile == null ? '' : mobile).replace(/[\s-]/g, '');
    if (!/^0\d{9}$/.test(digits)) {
      throw new Error('Patient mobile number must contain 10 digits and start with 0.');
    }
    return digits;
  }

  get formattedMobile() {
    return `${this.mobile.slice(0, 4)} ${this.mobile.slice(4, 7)} ${this.mobile.slice(7)}`;
  }
}

/** A booking that links a patient, a professional and a time slot. */
class Appointment {
  constructor(patient, professional, slot) {
    if (!(patient instanceof Patient)) {
      throw new Error('An appointment needs a patient.');
    }
    if (!(professional instanceof HealthProfessional)) {
      throw new Error('An appointment needs a health professional.');
    }
    if (!(slot instanceof TimeSlot)) {
      throw new Error('An appointment needs a time slot.');
    }
    this.id = Appointment.nextId();
    this.patient = patient;
    this.professional = professional;
    this.slot = slot;
  }

  static nextId() {
    Appointment.sequence = (Appointment.sequence || 0) + 1;
    return `apt-${Date.now().toString(36)}-${Appointment.sequence}`;
  }

  /** Orders by time, then professional ID, then patient name. */
  compareTo(other) {
    if (this.slot.startMinute !== other.slot.startMinute) {
      return this.slot.startMinute - other.slot.startMinute;
    }
    if (this.professional.id !== other.professional.id) {
      return this.professional.id.localeCompare(other.professional.id);
    }
    return this.patient.name.localeCompare(other.patient.name);
  }

  /** Plain data stored in localStorage. */
  toJSON() {
    return {
      patientName: this.patient.name,
      mobile: this.patient.mobile,
      professionalId: this.professional.id,
      start: this.slot.startMinute,
    };
  }
}

/** Owns every appointment for the day and enforces the booking rules. */
class AppointmentManager {
  constructor(initial = []) {
    this.appointments = [];
    initial.forEach((appointment) => this.appointments.push(appointment));
  }

  get count() {
    return this.appointments.length;
  }

  /** @returns {Appointment[]} a sorted copy */
  all() {
    return [...this.appointments].sort((a, b) => a.compareTo(b));
  }

  getById(id) {
    return this.appointments.find((appointment) => appointment.id === id) || null;
  }

  isBooked(professionalId, startMinute) {
    return this.appointments.some(
      (appointment) => appointment.professional.id === professionalId
        && appointment.slot.startMinute === startMinute,
    );
  }

  bookedStarts(professionalId) {
    return new Set(
      this.appointments
        .filter((appointment) => appointment.professional.id === professionalId)
        .map((appointment) => appointment.slot.startMinute),
    );
  }

  /**
   * Adds an appointment unless the professional is already booked in that
   * slot.
   * @returns {{ok: boolean, message: string, appointment?: Appointment}}
   */
  add(appointment) {
    if (!(appointment instanceof Appointment)) {
      return { ok: false, message: 'The appointment is empty and was not added.' };
    }
    if (this.isBooked(appointment.professional.id, appointment.slot.startMinute)) {
      return {
        ok: false,
        message: `${appointment.professional.name} is already booked at ${appointment.slot.rangeLabel}. Please choose another time.`,
      };
    }
    this.appointments.push(appointment);
    return {
      ok: true,
      message: `${appointment.patient.name} is booked with ${appointment.professional.name} at ${appointment.slot.rangeLabel}.`,
      appointment,
    };
  }

  /** Cancels by record ID. */
  cancelById(id) {
    const index = this.appointments.findIndex((appointment) => appointment.id === id);
    if (index === -1) {
      return { ok: false, message: 'That appointment no longer exists.' };
    }
    const [removed] = this.appointments.splice(index, 1);
    return {
      ok: true,
      message: `Cancelled ${removed.patient.name}'s appointment with ${removed.professional.name} at ${removed.slot.rangeLabel}.`,
      appointment: removed,
    };
  }

  /** Cancels by health professional ID and start time. */
  cancel(professionalId, startMinute) {
    const index = this.appointments.findIndex(
      (appointment) => appointment.professional.id === professionalId
        && appointment.slot.startMinute === startMinute,
    );
    if (index === -1) {
      return { ok: false, message: 'No matching appointment was found.' };
    }
    return this.cancelById(this.appointments[index].id);
  }

  /** Cancels the earliest appointment that belongs to a mobile number. */
  cancelByMobile(mobile) {
    let earliest = null;
    this.appointments.forEach((appointment) => {
      if (appointment.patient.mobile === mobile
        && (!earliest || appointment.slot.startMinute < earliest.slot.startMinute)) {
        earliest = appointment;
      }
    });
    if (!earliest) {
      return { ok: false, message: `No appointment was found for mobile number ${mobile}.` };
    }
    return this.cancelById(earliest.id);
  }

  byProfessional(professionalId) {
    return this.appointments
      .filter((appointment) => appointment.professional.id === professionalId)
      .sort((a, b) => a.compareTo(b));
  }

  byMobile(mobile) {
    return this.appointments
      .filter((appointment) => appointment.patient.mobile === mobile)
      .sort((a, b) => a.compareTo(b));
  }
}

/** The seven professionals that work at the clinic. */
const PROFESSIONALS = Object.freeze([
  new GeneralPractitioner('1001', 'Dr Alice Chen', ['Monday', 'Wednesday', 'Friday'], 'Room 12'),
  new GeneralPractitioner('1002', 'Dr Brian Lee', ['Tuesday', 'Thursday'], 'Room 8'),
  new GeneralPractitioner('1003', 'Dr Carol Diaz', ['Monday', 'Tuesday', 'Thursday'], 'Room 15'),
  new Nurse('2001', 'Nurse Emma Wilson', ['Monday', 'Tuesday', 'Friday'], 'Outpatient Department'),
  new Nurse('2002', 'Nurse Liam Patel', ['Wednesday', 'Thursday', 'Saturday'], 'Emergency Department'),
  new Surgeon('3001', 'Dr Nora Ibrahim', ['Tuesday', 'Friday'], 'Orthopaedic Surgery'),
  new Dietitian('4001', 'Ms Grace Kim', ['Wednesday', 'Thursday'], 'Clinical Nutrition'),
]);

/**
 * Builds the demo day that is loaded on the first visit.
 * @returns {Appointment[]}
 */
function createSeedAppointments() {
  const professionalById = new Map(PROFESSIONALS.map((professional) => [professional.id, professional]));
  const rows = [
    ['08:30', 'Mary Johnson', '0423456789', '1003'],
    ['09:00', 'John Smith', '0412345678', '1001'],
    ['09:30', 'Sophie Taylor', '0445678901', '2001'],
    ['10:00', 'David Brown', '0434567890', '3001'],
    ['10:30', 'Li Wei', '0455666777', '4001'],
    ['11:00', 'Emma Clarke', '0466777888', '2002'],
    ['13:00', 'John Smith', '0412345678', '1002'],
    ['14:30', 'Mary Johnson', '0423456789', '4001'],
  ];

  return rows.map(([time, name, mobile, professionalId]) => new Appointment(
    new Patient(name, mobile),
    professionalById.get(professionalId),
    TimeSlot.fromText(time),
  ));
}

const MODEL = {
  CLINIC,
  TYPE_SHORT,
  TimeSlot,
  HealthProfessional,
  GeneralPractitioner,
  Nurse,
  Surgeon,
  Dietitian,
  Patient,
  Appointment,
  AppointmentManager,
  PROFESSIONALS,
  createSeedAppointments,
};

if (typeof window !== 'undefined') {
  window.AppModel = MODEL;
}
if (typeof module !== 'undefined' && module.exports) {
  module.exports = MODEL;
}
