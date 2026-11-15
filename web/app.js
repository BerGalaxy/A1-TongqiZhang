'use strict';

/**
 * User interface for the Clinic Appointment System web demo.
 *
 * The file reads the domain classes from window.AppModel, keeps the current
 * appointments in localStorage when the browser allows it, and renders the
 * overview, booking form, appointment book and team pages.
 */

(function () {
    const {
      TimeSlot,
      PROFESSIONALS,
      Patient,
      Appointment,
      AppointmentManager,
      createSeedAppointments,
    } = window.AppModel;

  const STORAGE_KEY = 'clarity-clinic-appointments-v1';
  const VIEWS = ['overview', 'book', 'appointments', 'team'];
  const VIEW_TITLES = {
    overview: 'Overview',
    book: 'Book an appointment',
    appointments: 'Appointments',
    team: 'Our team',
  };

  const professionalsById = new Map(PROFESSIONALS.map((professional) => [professional.id, professional]));

  const state = {
    selectedProfessionalId: null,
    selectedSlot: null,
    highlightId: null,
    pendingCancelId: null,
  };

  let manager = new AppointmentManager();

  /* ------------------------------------------------------------------ */
  /* Small helpers                                                       */
  /* ------------------------------------------------------------------ */

  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

  function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, (character) => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;',
    }[character]));
  }

  function typeBadge(professional) {
    return `<span class="badge badge--${professional.typeKey}">${escapeHtml(professional.typeShort)}</span>`;
  }

  function avatar(professional, extraClass = '') {
    return `<span class="avatar avatar--${professional.typeKey} ${extraClass}" aria-hidden="true">${escapeHtml(professional.initials)}</span>`;
  }

  function emptyState(title, text, showClearButton) {
    return `
      <div class="empty-state">
        <h3>${escapeHtml(title)}</h3>
        <p>${escapeHtml(text)}</p>
        ${showClearButton ? '<button class="btn btn-soft btn-small" type="button" data-clear-filters>Clear filters</button>' : ''}
      </div>`;
  }

  function storageAvailable() {
    try {
      const key = '__clarity_storage_test__';
      window.localStorage.setItem(key, '1');
      window.localStorage.removeItem(key);
      return true;
    } catch (error) {
      return false;
    }
  }

  const canStore = storageAvailable();

  /* ------------------------------------------------------------------ */
  /* Persistence                                                         */
  /* ------------------------------------------------------------------ */

  function loadManager() {
    if (!canStore) {
      return null;
    }
    try {
      const raw = window.localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return null;
      }
      const records = JSON.parse(raw);
      if (!Array.isArray(records)) {
        return null;
      }
      const rebuilt = new AppointmentManager();
      records.forEach((record) => {
        const professional = professionalsById.get(String(record.professionalId));
        const slot = TimeSlot.fromMinutes(Number(record.start));
        if (!professional || !slot) {
          return;
        }
        try {
          rebuilt.add(new Appointment(
            new Patient(record.patientName, record.mobile),
            professional,
            slot,
          ));
        } catch (error) {
          // Ignore a stored record that is no longer valid.
        }
      });
      return rebuilt;
    } catch (error) {
      return null;
    }
  }

  function save() {
    if (!canStore) {
      return;
    }
    try {
      const records = manager.all().map((appointment) => appointment.toJSON());
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(records));
    } catch (error) {
      // Storage can fill up or be blocked; the session keeps working in memory.
    }
  }

  /* ------------------------------------------------------------------ */
  /* Rendering                                                           */
  /* ------------------------------------------------------------------ */

  function renderStats() {
    const total = manager.count;
    const capacity = PROFESSIONALS.length * TimeSlot.dailySchedule().length;
    const patients = new Set(manager.all().map((appointment) => appointment.patient.mobile)).size;

    $('#stat-appointments').textContent = total;
    $('#stat-free').textContent = capacity - total;
    $('#stat-professionals').textContent = PROFESSIONALS.length;
    $('#stat-patients').textContent = patients;
    $('#stat-capacity').textContent = `Out of ${capacity} bookable slots`;
    $('#nav-count').textContent = total;
    $('#hero-summary').textContent = `${total} appointment${total === 1 ? '' : 's'} booked today - `
      + `${capacity - total} of ${capacity} slots still open.`;
  }

  function renderHeroNext() {
    const container = $('#hero-next');
    const all = manager.all();

    if (!all.length) {
      container.innerHTML = `
        <p class="hero-next-label">Today</p>
        <p class="hero-next-empty">No appointments booked yet.</p>
        <button class="btn btn-light" type="button" data-go="book">Book the first one</button>`;
      return;
    }

    const now = new Date();
    const nowMinutes = now.getHours() * 60 + now.getMinutes();
    const upcoming = all.find((appointment) => appointment.slot.startMinute >= nowMinutes);
    const next = upcoming || all[0];

    container.innerHTML = `
      <p class="hero-next-label">${upcoming ? 'Next appointment' : "Today's first appointment"}</p>
      <p class="hero-next-time">${next.slot.label}</p>
      <p class="hero-next-patient">${escapeHtml(next.patient.name)}</p>
      <p class="hero-next-professional">${escapeHtml(next.professional.name)} - ${escapeHtml(next.professional.type)}</p>
      <div class="hero-next-foot">
        ${typeBadge(next.professional)}
        <span>${escapeHtml(next.professional.detail)}</span>
      </div>`;
  }

  function renderTimeline() {
    const container = $('#overview-timeline');
    const all = manager.all();

    if (!all.length) {
      container.innerHTML = emptyState(
        'No appointments yet',
        'The day is clear. Book the first appointment to get started.',
        false,
      );
      return;
    }

    container.innerHTML = `
      <ul class="timeline">
        ${all.map((appointment) => `
          <li class="timeline-item ${state.highlightId === appointment.id ? 'is-new' : ''}">
            <span class="timeline-time">${appointment.slot.label}</span>
            <span class="timeline-dot type-${appointment.professional.typeKey}" aria-hidden="true"></span>
            <div class="timeline-main">
              <strong>${escapeHtml(appointment.patient.name)}</strong>
              <span>${escapeHtml(appointment.professional.name)} - ${escapeHtml(appointment.professional.detail)}</span>
            </div>
            ${typeBadge(appointment.professional)}
          </li>`).join('')}
      </ul>`;
  }

  function renderOverviewTeam() {
    const container = $('#overview-team');
    container.innerHTML = PROFESSIONALS.map((professional) => `
      <div class="team-row">
        ${avatar(professional)}
        <div class="team-row-main">
          <strong>${escapeHtml(professional.name)}</strong>
          <span>${escapeHtml(professional.type)} - ${escapeHtml(professional.daysShort)}</span>
        </div>
        <button class="btn btn-secondary btn-small" type="button"
          data-book-professional="${professional.id}"
          aria-label="Book with ${escapeHtml(professional.name)}">
          Book
        </button>
      </div>`).join('');
  }

  function renderProfessionalPicker() {
    const container = $('#professional-picker');
    container.innerHTML = PROFESSIONALS.map((professional) => {
      const selected = professional.id === state.selectedProfessionalId;
      return `
        <button class="pro-card ${selected ? 'is-selected' : ''}" type="button"
          data-professional="${professional.id}" aria-pressed="${selected}">
          ${avatar(professional)}
          <span class="pro-card-main">
            <span class="pro-card-name">${escapeHtml(professional.name)}</span>
            <span class="pro-card-meta">${escapeHtml(professional.detail)} - ${escapeHtml(professional.daysShort)}</span>
          </span>
          ${typeBadge(professional)}
          <span class="pro-check" aria-hidden="true">&#10003;</span>
        </button>`;
    }).join('');
  }

  function slotGroup(title, slots, professional, booked) {
    const buttons = slots.map((slot) => {
      const isBooked = booked.has(slot.startMinute);
      const isSelected = state.selectedSlot && state.selectedSlot.startMinute === slot.startMinute;
      const disabled = !professional || isBooked;
      return `
        <button class="slot ${isBooked ? 'is-booked' : ''} ${isSelected ? 'is-selected' : ''}"
          type="button" data-start="${slot.startMinute}" ${disabled ? 'disabled' : ''}
          aria-pressed="${isSelected}" title="${isBooked ? 'Already booked' : `Book ${slot.label}`}">
          <span>${slot.label}</span>
          <small>${isBooked ? 'Booked' : '30 min'}</small>
        </button>`;
    }).join('');

    return `
      <div class="slot-group">
        <p class="slot-group-title">${title}</p>
        <div class="slot-grid">${buttons}</div>
      </div>`;
  }

  function renderSlots() {
    const professional = state.selectedProfessionalId
      ? professionalsById.get(state.selectedProfessionalId)
      : null;
    const booked = professional ? manager.bookedStarts(professional.id) : new Set();
    const schedule = TimeSlot.dailySchedule();
    const morning = schedule.filter((slot) => slot.startMinute < 12 * 60);
    const afternoon = schedule.filter((slot) => slot.startMinute >= 12 * 60);
    const hint = $('#slot-hint');

    if (!professional) {
      hint.textContent = 'Select a professional to see live availability.';
    } else {
      hint.textContent = `${professional.name} has ${schedule.length - booked.size} of ${schedule.length} `
        + 'slots free today. Grey slots are already booked.';
    }

    $('#slot-picker').innerHTML = [
      slotGroup('Morning', morning, professional, booked),
      slotGroup('Afternoon', afternoon, professional, booked),
    ].join('');
  }

  function setSummaryValue(selector, text) {
    const element = $(selector);
    element.textContent = text;
    element.classList.toggle('is-empty', text === 'Not selected');
  }

  function updateSummary() {
    const professional = state.selectedProfessionalId
      ? professionalsById.get(state.selectedProfessionalId)
      : null;
    const name = $('#patient-name').value.trim();
    const mobile = $('#patient-mobile').value.trim();

    setSummaryValue('#summary-professional', professional ? `${professional.name} - ${professional.type}` : 'Not selected');
    setSummaryValue('#summary-time', state.selectedSlot ? state.selectedSlot.rangeLabel : 'Not selected');
    setSummaryValue('#summary-patient', name || 'Not selected');
    setSummaryValue('#summary-mobile', mobile || 'Not selected');
  }

  function appointmentMatches(appointment, query) {
    const haystack = [
      appointment.patient.name,
      appointment.patient.mobile,
      appointment.patient.formattedMobile,
      appointment.professional.name,
      appointment.professional.type,
      appointment.professional.detail,
      appointment.slot.label,
      appointment.slot.rangeLabel,
    ].join(' ').toLowerCase();
    return haystack.includes(query);
  }

  function renderAppointments() {
    const query = $('#appointment-search').value.trim().toLowerCase();
    const professionalId = $('#professional-filter').value || 'all';

    if (!manager.count) {
      $('#appointment-count').textContent = '0 appointments';
      $('#appointment-list').innerHTML = emptyState(
        'The day is still clear',
        'No appointments have been booked yet. Use the Book page to add one.',
        false,
      );
      return;
    }

    let list = manager.all();
    if (professionalId !== 'all') {
      list = list.filter((appointment) => appointment.professional.id === professionalId);
    }
    if (query) {
      list = list.filter((appointment) => appointmentMatches(appointment, query));
    }

    $('#appointment-count').textContent = `${list.length} appointment${list.length === 1 ? '' : 's'}`;

    if (!list.length) {
      $('#appointment-list').innerHTML = emptyState(
        'No appointments found',
        'Try a different name, mobile number or professional.',
        query !== '' || professionalId !== 'all',
      );
      return;
    }

    $('#appointment-list').innerHTML = list.map((appointment) => `
      <article class="apt-card type-${appointment.professional.typeKey} ${state.highlightId === appointment.id ? 'is-new' : ''}">
        <div class="apt-time">
          <strong>${appointment.slot.label}</strong>
          <span>30 min</span>
        </div>
        <div class="apt-main">
          <div class="apt-patient">
            <strong>${escapeHtml(appointment.patient.name)}</strong>
            <span>${escapeHtml(appointment.patient.formattedMobile)}</span>
          </div>
          <div class="apt-professional">
            ${typeBadge(appointment.professional)}
            <span>${escapeHtml(appointment.professional.name)}</span>
            <span class="muted">${escapeHtml(appointment.professional.detail)}</span>
          </div>
        </div>
        <button class="btn btn-ghost-danger" type="button" data-cancel-id="${appointment.id}">
          Cancel
        </button>
      </article>`).join('');
  }

  function renderTeamGrid() {
    $('#team-grid').innerHTML = PROFESSIONALS.map((professional) => `
      <article class="team-card type-${professional.typeKey}">
        <div class="team-card-top">
          ${avatar(professional, 'avatar--lg')}
          ${typeBadge(professional)}
        </div>
        <div>
          <h3>${escapeHtml(professional.name)}</h3>
          <p class="team-meta">ID ${professional.id} - ${escapeHtml(professional.type)}</p>
        </div>
        <div class="chips">
          ${professional.days.map((day) => `<span class="chip-day">${escapeHtml(day.slice(0, 3))}</span>`).join('')}
        </div>
        <p class="team-detail"><span>${escapeHtml(professional.detailLabel)}</span>${escapeHtml(professional.detail)}</p>
        <p class="team-duty">${escapeHtml(professional.duty)}</p>
        <button class="btn btn-soft btn-small" type="button" data-book-professional="${professional.id}">
          Book appointment
        </button>
      </article>`).join('');
  }

  function refresh() {
    renderStats();
    renderHeroNext();
    renderTimeline();
    renderOverviewTeam();
    renderProfessionalPicker();
    renderSlots();
    updateSummary();
    renderAppointments();
    renderTeamGrid();
  }

  /* ------------------------------------------------------------------ */
  /* Navigation and views                                                */
  /* ------------------------------------------------------------------ */

  function viewFromHash() {
    const view = window.location.hash.replace('#', '');
    return VIEWS.includes(view) ? view : 'overview';
  }

  function renderView(view) {
    const target = VIEWS.includes(view) ? view : 'overview';

    $$('.view').forEach((panel) => {
      panel.hidden = panel.dataset.viewPanel !== target;
    });
    $$('.nav-item').forEach((link) => {
      const active = link.dataset.view === target;
      link.classList.toggle('is-active', active);
      if (active) {
        link.setAttribute('aria-current', 'page');
      } else {
        link.removeAttribute('aria-current');
      }
    });

    $('#view-title').textContent = VIEW_TITLES[target];
    document.title = `${VIEW_TITLES[target]} - Clinic Appointment System`;

    if (target === 'book') {
      renderProfessionalPicker();
      renderSlots();
      updateSummary();
    } else if (target === 'appointments') {
      renderAppointments();
    } else if (target === 'team') {
      renderTeamGrid();
    }

    window.scrollTo(0, 0);
  }

  function navigate(view) {
    if (window.location.hash === `#${view}`) {
      renderView(view);
    } else {
      window.location.hash = view;
    }
  }

  /* ------------------------------------------------------------------ */
  /* Booking flow                                                        */
  /* ------------------------------------------------------------------ */

  function setError(id, message) {
    const element = document.getElementById(id);
    if (element) {
      element.textContent = message;
    }
  }

  function clearError(id) {
    setError(id, '');
  }

  function selectProfessional(professionalId) {
    if (!professionalsById.has(professionalId)) {
      return;
    }
    state.selectedProfessionalId = professionalId;

    if (state.selectedSlot && manager.isBooked(professionalId, state.selectedSlot.startMinute)) {
      state.selectedSlot = null;
    }

    clearError('professional-error');
    clearError('slot-error');
    renderProfessionalPicker();
    renderSlots();
    updateSummary();
  }

  function selectSlot(startMinute) {
    const slot = TimeSlot.fromMinutes(startMinute);
    if (!slot) {
      return;
    }
    state.selectedSlot = slot;
    clearError('slot-error');
    renderSlots();
    updateSummary();
  }

  function clearBookingForm() {
    state.selectedProfessionalId = null;
    state.selectedSlot = null;
    $('#patient-name').value = '';
    $('#patient-mobile').value = '';
    ['name-error', 'mobile-error', 'professional-error', 'slot-error'].forEach(clearError);
    updateSummary();
  }

  function handleBookingSubmit(event) {
    event.preventDefault();

    const professional = state.selectedProfessionalId
      ? professionalsById.get(state.selectedProfessionalId)
      : null;
    const name = $('#patient-name').value.trim();
    const mobile = $('#patient-mobile').value.trim();

    let valid = true;

    if (name.length < 2) {
      setError('name-error', "Please enter the patient's full name.");
      valid = false;
    } else {
      clearError('name-error');
    }

    try {
      Patient.normaliseMobile(mobile);
      clearError('mobile-error');
    } catch (error) {
      setError('mobile-error', 'Enter a 10-digit mobile number that starts with 0.');
      valid = false;
    }

    if (!professional) {
      setError('professional-error', 'Choose a health professional.');
      valid = false;
    }
    if (!state.selectedSlot) {
      setError('slot-error', 'Choose a time slot.');
      valid = false;
    }

    if (professional && state.selectedSlot && manager.isBooked(professional.id, state.selectedSlot.startMinute)) {
      setError('slot-error', 'That time was just booked. Please choose another slot.');
      valid = false;
      renderSlots();
    }

    if (!valid) {
      toast('Please check the highlighted fields.', 'warning');
      const firstError = $('.field-error:not(:empty)');
      if (firstError) {
        firstError.scrollIntoView({ block: 'center', behavior: 'smooth' });
      }
      return;
    }

    let appointment;
    try {
      appointment = new Appointment(new Patient(name, mobile), professional, state.selectedSlot);
    } catch (error) {
      toast('Please check the patient details and try again.', 'warning');
      return;
    }

    const result = manager.add(appointment);
    if (!result.ok) {
      toast(result.message, 'warning');
      renderSlots();
      return;
    }

    save();
    state.highlightId = appointment.id;
    clearBookingForm();
    $('#appointment-search').value = '';
    $('#professional-filter').value = 'all';
    refresh();
    navigate('appointments');
    toast(result.message, 'success');

    window.setTimeout(() => {
      state.highlightId = null;
      const highlighted = $('.apt-card.is-new');
      if (highlighted) {
        highlighted.classList.remove('is-new');
      }
    }, 2600);
  }

  /* ------------------------------------------------------------------ */
  /* Cancelling and resetting                                            */
  /* ------------------------------------------------------------------ */

  function openCancelDialog(appointmentId) {
    const appointment = manager.getById(appointmentId);
    if (!appointment) {
      return;
    }
    state.pendingCancelId = appointmentId;
    $('#cancel-dialog-text').textContent = `${appointment.patient.name} - ${appointment.professional.name} - ${appointment.slot.rangeLabel}`;
    $('#cancel-dialog').showModal();
  }

  function confirmCancel() {
    const result = manager.cancelById(state.pendingCancelId);
    $('#cancel-dialog').close();
    if (result.ok) {
      save();
      refresh();
      toast(result.message, 'success');
    } else {
      toast(result.message, 'warning');
    }
    state.pendingCancelId = null;
  }

  function openResetDialog() {
    $('#reset-dialog').showModal();
  }

  function confirmReset() {
    manager = new AppointmentManager(createSeedAppointments());
    save();
    $('#reset-dialog').close();
    clearBookingForm();
    refresh();
    toast('The demo day has been restored.', 'info');
  }

  /* ------------------------------------------------------------------ */
  /* Toasts                                                              */
  /* ------------------------------------------------------------------ */

  function toast(message, type = 'success') {
    const container = $('#toasts');

    // Keep the stack short and replace an identical message instead of
    // repeating it when the user submits the same invalid form twice.
    const duplicate = Array.from(container.querySelectorAll('.toast p'))
      .find((paragraph) => paragraph.textContent === message);
    if (duplicate) {
      duplicate.closest('.toast').remove();
    }
    while (container.children.length >= 2) {
      container.firstElementChild.remove();
    }

    const element = document.createElement('div');
    element.className = `toast toast--${type}`;
    element.setAttribute('role', type === 'warning' ? 'alert' : 'status');
    element.innerHTML = `
      <p>${escapeHtml(message)}</p>
      <button class="toast-close" type="button" aria-label="Dismiss">&#10005;</button>`;

    element.querySelector('.toast-close').addEventListener('click', () => element.remove());
    container.appendChild(element);

    window.setTimeout(() => {
      element.classList.add('is-leaving');
      window.setTimeout(() => element.remove(), 220);
    }, 3800);
  }

  /* ------------------------------------------------------------------ */
  /* Event wiring                                                        */
  /* ------------------------------------------------------------------ */

  function bindEvents() {
    document.addEventListener('click', (event) => {
      const target = event.target instanceof Element ? event.target : null;
      if (!target) {
        return;
      }

      const goButton = target.closest('[data-go]');
      if (goButton) {
        navigate(goButton.dataset.go);
        return;
      }

      const professionalButton = target.closest('[data-professional]');
      if (professionalButton) {
        selectProfessional(professionalButton.dataset.professional);
        return;
      }

      const slotButton = target.closest('[data-start]');
      if (slotButton && !slotButton.disabled) {
        selectSlot(Number(slotButton.dataset.start));
        return;
      }

      const bookButton = target.closest('[data-book-professional]');
      if (bookButton) {
        selectProfessional(bookButton.dataset.bookProfessional);
        navigate('book');
        return;
      }

      const cancelButton = target.closest('[data-cancel-id]');
      if (cancelButton) {
        openCancelDialog(cancelButton.dataset.cancelId);
        return;
      }

      const closeButton = target.closest('[data-close-dialog]');
      if (closeButton) {
        closeButton.closest('dialog').close();
        return;
      }

      if (target.closest('[data-clear-filters]')) {
        $('#appointment-search').value = '';
        $('#professional-filter').value = 'all';
        renderAppointments();
      }
    });

    $('#booking-form').addEventListener('submit', handleBookingSubmit);
    $('#patient-name').addEventListener('input', () => {
      clearError('name-error');
      updateSummary();
    });
    $('#patient-mobile').addEventListener('input', () => {
      clearError('mobile-error');
      updateSummary();
    });
    $('#appointment-search').addEventListener('input', renderAppointments);
    $('#professional-filter').addEventListener('change', renderAppointments);
    $('#confirm-cancel').addEventListener('click', confirmCancel);
    $('#reset-demo').addEventListener('click', openResetDialog);
    $('#confirm-reset').addEventListener('click', confirmReset);

    $$('dialog').forEach((dialog) => {
      dialog.addEventListener('click', (event) => {
        if (event.target === dialog) {
          dialog.close();
        }
      });
    });

    window.addEventListener('hashchange', () => renderView(viewFromHash()));
  }

  /* ------------------------------------------------------------------ */
  /* Start-up                                                            */
  /* ------------------------------------------------------------------ */

  function fillProfessionalFilter() {
    const options = ['<option value="all">All professionals</option>'];
    PROFESSIONALS.forEach((professional) => {
      options.push(`<option value="${professional.id}">${escapeHtml(professional.name)} - ${escapeHtml(professional.typeShort)}</option>`);
    });
    $('#professional-filter').innerHTML = options.join('');
  }

  function init() {
    const stored = loadManager();
    manager = stored || new AppointmentManager(createSeedAppointments());
    if (!stored) {
      save();
    }

    $('#today-label').textContent = new Intl.DateTimeFormat('en-AU', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }).format(new Date());

    $('#storage-note').textContent = canStore
      ? 'Appointments are saved in this browser.'
      : 'Browser storage is blocked, so appointments will be lost when this page is closed.';

    fillProfessionalFilter();
    bindEvents();
    refresh();
    renderView(viewFromHash());
  }

  init();
})();
