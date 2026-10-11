# Lessons of Wellbeing from Cats <img src="./docs/mia-head-logo.png" alt="Mia" height="30"> <img src="./docs/thor-head-logo.png" alt="Thor" height="30">

This is my individual final project for the Factoría F5 Full Stack Development Bootcamp.

The application introduces a wellbeing sanctuary inspired by the wisdom of cats, with Mia and Thor as its main characters. It combines feline-inspired self-care lessons with traditional practices such as yoga and Ayurveda, as well as kombucha and tea tasting sessions.

Visitors will be able to explore the sanctuary and its activities without logging in. An account will be required to book sessions and manage reservations.

## Project Structure

The project uses a decoupled architecture, with the frontend and backend maintained in separate repositories. This repository contains the backend.

- <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" alt="GitHub" width="20" height="20"> **[Frontend repository](https://github.com/Alexapop/Lessons-Of-Wellbeing-from-cats-frontend)**
- <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" alt="GitHub" width="20" height="20"> **[Backend repository](https://github.com/Alexapop/Lessons-Of-Wellbeing-from-cats-backend)**

## Planned Features

### Home & Sanctuary

- Explore the homepage without an account.
- Discover the sanctuary, meet Mia and Thor, and navigate to the class calendar from their profile section.
- Preview the Yoga, Ayurveda, and Kombucha & Tea Lounge sections and navigate to the calendar.
- Find the sanctuary's logo, opening hours, location, and payment information.
- Use a responsive layout across mobile, tablet, and desktop screens.

### Wellbeing Lessons

- Read feline-inspired self-care lessons.
- Expand and collapse individual lessons.
- Use the **Practice in Class** button to open the calendar with the relevant filters applied.

### Class Calendar

- Browse upcoming sessions without logging in.
- Expand and collapse class details.
- View each session's date, time, host, location, description, and duration.
- Filter sessions by category (such as Yoga, Ayurveda, or Kombucha & Tea Lounge) and by host.
- View a **Reserve** button when logged in or a **Login to Reserve** button when logged out.

### Registration & Login

- Create an account with a name, email address, phone number, location, username, and password.
- Register with a unique email address and username, then proceed to the login page.
- Log in with a username or email address and a password.
- See the user's name and a logout button in the header after signing in.
- Access the user dashboard after logging in, or the separate admin dashboard when signing in as an administrator.
- Log out and return to the homepage.

### Reservations & User Dashboard

- As a registered user who is logged in, select a session to reserve and open a booking form with your username or email address and phone number pre-filled.
- Provide tea or kombucha preferences when booking a Kombucha & Tea Lounge session. These preferences are required before confirming the booking.
- Confirm a reservation and return to the dashboard.
- View personal reservations, including their status, notes, date, host, and location.
- Cancel confirmed reservations from the dashboard.
- Keep cancelled reservations visible with a **Cancelled** status and no cancellation button.
- View payment instructions for confirmed bookings: **Cash or Bizum**.

### Admin Dashboard

- Access a separate dashboard restricted to administrators.
- View all reservations, including user information and booking notes.
- Create sessions and publish them in the calendar.
- Edit session details.
- Delete sessions so they no longer appear in the calendar.

## Project Documentation

Links to the project Wiki and supporting documentation will be added as they become available.

| Topic                           | Planned content                                                                    | Link        |
| ------------------------------- | ---------------------------------------------------------------------------------- | ----------- |
| Getting Started                 | Environment setup, installation, and instructions for running the project locally. | To be added |
| Agile Management & User Stories | Jira tracking, user stories, and acceptance criteria.                              | To be added |
| Architecture                    | Frontend and backend responsibilities, application structure, and API integration. | To be added |
| Security                        | Authentication, user and admin access, and protected reservations.                 | To be added |
| Testing                         | Instructions for running unit and end-to-end tests.                                | To be added |

<table align="center">
  <thead>
    <tr>
      <th>Developer</th>
      <th>GitHub profile</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alexa Pop</td>
      <td><img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" alt="GitHub" width="24" height="24"> <a href="https://github.com/Alexapop">@Alexapop</a></td>
    </tr>
  </tbody>
</table>
