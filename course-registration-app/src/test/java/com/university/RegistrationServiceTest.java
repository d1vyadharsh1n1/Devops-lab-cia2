package com.university;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRejectRegistrationWhenCourseIsAtCapacity() {
        Student student = new Student("S102", "Rahul");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        for (int i = 0; i < 30; i++) {
            course.incrementEnrollment();
        }

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(30, course.getCurrentEnrollment());
        assertTrue(service.getAuditLog().isEmpty());
    }

    @Test
    void shouldAllowRegistrationWhenExactlyOneSeatRemains() {
        Student student = new Student("S103", "Priya");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        for (int i = 0; i < 29; i++) {
            course.incrementEnrollment();
        }

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(30, course.getCurrentEnrollment());
        assertEquals(
                List.of("S103 -> CS301"),
                service.getAuditLog());
    }
    @Test
    void shouldRecordSuccessfulRegistrationInAuditLog() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        assertEquals(1, service.getAuditLog().size());
        assertEquals(List.of("S101 -> CS301"),service.getAuditLog());
    }

    @Test
    void shouldNotRecordRejectedRegistrationInAuditLog() {
        Student student = new Student("S102", "Rahul");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, false, null);

        RegistrationService service = new RegistrationService();

        assertFalse(service.register(student, course));

        assertTrue(service.getAuditLog().isEmpty());
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void failedAttemptShouldPreservePreviousAuditEntry() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null);

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        List<String> previousEntries = service.getAuditLog();
        assertFalse(service.register(student, course));
        assertEquals(previousEntries, service.getAuditLog());
        assertEquals(1, service.getAuditLog().size());
        assertEquals(List.of("S101 -> CS301"), service.getAuditLog());
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRegisterStudentForValidCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                false,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }

    @Test
    void shouldRejectMissingPrerequisite() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {

        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401",
                "Advanced Algorithms",
                4,
                30,
                true,
                "CS301"
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {

        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301",
                "Data Structures",
                4,
                30,
                true,
                null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
    }
}