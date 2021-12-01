///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.optaplanner:optaplanner-core:8.14.0.Final
//DEPS ch.qos.logback:logback-classic:1.2.3

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class optaplannerHelloWorld {

    private static final Logger LOGGER = LoggerFactory.getLogger(optaplannerHelloWorld.class);

    public static void main(String[] args) {
        LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
        logCtx.getLogger("org.kie").setLevel(Level.INFO);
        logCtx.getLogger("org.drools").setLevel(Level.INFO);
        logCtx.getLogger("org.optaplanner").setLevel(Level.INFO);

        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Lesson.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = generateDemoData();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
    }

    public static TimeTable generateDemoData() {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        List<Room> roomList = new ArrayList<>(3);
        roomList.add(new Room("Room A"));
        roomList.add(new Room("Room B"));
        roomList.add(new Room("Room C"));

        List<Lesson> lessonList = new ArrayList<>();
        long id = 0;
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "9th grade"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "9th grade"));
        lessonList.add(new Lesson(id++, "Physics", "M. Curie", "9th grade"));
        lessonList.add(new Lesson(id++, "Chemistry", "M. Curie", "9th grade"));
        lessonList.add(new Lesson(id++, "Biology", "C. Darwin", "9th grade"));
        lessonList.add(new Lesson(id++, "History", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(id++, "English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(id++, "English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(id++, "Spanish", "P. Cruz", "9th grade"));
        lessonList.add(new Lesson(id++, "Spanish", "P. Cruz", "9th grade"));

        lessonList.add(new Lesson(id++, "Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson(id++, "Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson(id++, "Physics", "M. Curie", "10th grade"));
        lessonList.add(new Lesson(id++, "Chemistry", "M. Curie", "10th grade"));
        lessonList.add(new Lesson(id++, "French", "M. Curie", "10th grade"));
        lessonList.add(new Lesson(id++, "Geography", "C. Darwin", "10th grade"));
        lessonList.add(new Lesson(id++, "History", "I. Jones", "10th grade"));
        lessonList.add(new Lesson(id++, "English", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson(id++, "Spanish", "P. Cruz", "10th grade"));

        return new TimeTable(timeslotList, roomList, lessonList);
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<Room> roomList = timeTable.getRoomList();
        List<Lesson> lessonList = timeTable.getLessonList();
        Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() != null)
                .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors.groupingBy(Lesson::getRoom)));
        LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
        LOGGER.info("|            | " + roomList.stream()
                .map(room -> String.format("%-10s", room.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Lesson>> cellList = roomList.stream()
                    .map(room -> {
                        Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
                        if (byRoomMap == null) {
                            return Collections.<Lesson>emptyList();
                        }
                        List<Lesson> cellLessonList = byRoomMap.get(room);
                        if (cellLessonList == null) {
                            return Collections.<Lesson>emptyList();
                        }
                        return cellLessonList;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getSubject).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getTeacher).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                    cellLessonList.stream().map(Lesson::getStudentGroup).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
        }
        List<Lesson> unassignedLessons = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() == null || lesson.getRoom() == null)
                .collect(Collectors.toList());
        if (!unassignedLessons.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Lesson lesson : unassignedLessons) {
                LOGGER.info("  " + lesson.getSubject() + " - " + lesson.getTeacher() + " - " + lesson.getStudentGroup());
            }
        }
    }

    @PlanningSolution
    public static class TimeTable {

        @ProblemFactCollectionProperty
        @ValueRangeProvider(id = "timeslotRange")
        private List<Timeslot> timeslotList;
        @ProblemFactCollectionProperty
        @ValueRangeProvider(id = "roomRange")
        private List<Room> roomList;
        @PlanningEntityCollectionProperty
        private List<Lesson> lessonList;

        @PlanningScore
        private HardSoftScore score;

        // No-arg constructor required for OptaPlanner
        public TimeTable() {
        }

        public TimeTable(List<Timeslot> timeslotList, List<Room> roomList, List<Lesson> lessonList) {
            this.timeslotList = timeslotList;
            this.roomList = roomList;
            this.lessonList = lessonList;
        }

        // ************************************************************************
        // Getters and setters
        // ************************************************************************

        public List<Timeslot> getTimeslotList() {
            return timeslotList;
        }

        public List<Room> getRoomList() {
            return roomList;
        }

        public List<Lesson> getLessonList() {
            return lessonList;
        }

        public HardSoftScore getScore() {
            return score;
        }

    }

    public static class Timeslot {

        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

        public Timeslot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Timeslot(DayOfWeek dayOfWeek, LocalTime startTime) {
            this(dayOfWeek, startTime, startTime.plusMinutes(50));
        }

        @Override
        public String toString() {
            return dayOfWeek + " " + startTime;
        }

        // ************************************************************************
        // Getters and setters
        // ************************************************************************

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

    }

    public static class Room {

        private String name;

        public Room(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        // ************************************************************************
        // Getters and setters
        // ************************************************************************

        public String getName() {
            return name;
        }

    }

    @PlanningEntity
    public static class Lesson {

        @PlanningId
        private Long id;

        private String subject;
        private String teacher;
        private String studentGroup;

        @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
        private Timeslot timeslot;
        @PlanningVariable(valueRangeProviderRefs = "roomRange")
        private Room room;

        // No-arg constructor required for OptaPlanner
        public Lesson() {
        }

        public Lesson(long id, String subject, String teacher, String studentGroup) {
            this.id = id;
            this.subject = subject;
            this.teacher = teacher;
            this.studentGroup = studentGroup;
        }

        public Lesson(long id, String subject, String teacher, String studentGroup, Timeslot timeslot, Room room) {
            this(id, subject, teacher, studentGroup);
            this.timeslot = timeslot;
            this.room = room;
        }

        @Override
        public String toString() {
            return subject + "(" + id + ")";
        }

        // ************************************************************************
        // Getters and setters
        // ************************************************************************

        public Long getId() {
            return id;
        }

        public String getSubject() {
            return subject;
        }

        public String getTeacher() {
            return teacher;
        }

        public String getStudentGroup() {
            return studentGroup;
        }

        public Timeslot getTimeslot() {
            return timeslot;
        }

        public void setTimeslot(Timeslot timeslot) {
            this.timeslot = timeslot;
        }

        public Room getRoom() {
            return room;
        }

        public void setRoom(Room room) {
            this.room = room;
        }

    }

    public static class TimeTableConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[] {
                    // Hard constraints
                    roomConflict(constraintFactory),
                    teacherConflict(constraintFactory),
                    studentGroupConflict(constraintFactory),
                    // Soft constraints
                    teacherRoomStability(constraintFactory),
                    teacherTimeEfficiency(constraintFactory),
                    studentGroupSubjectVariety(constraintFactory)
            };
        }

        Constraint roomConflict(ConstraintFactory constraintFactory) {
            // A room can accommodate at most one lesson at the same time.
            return constraintFactory
                    // Select each pair of 2 different lessons ...
                    .forEachUniquePair(Lesson.class,
                            // ... in the same timeslot ...
                            Joiners.equal(Lesson::getTimeslot),
                            // ... in the same room ...
                            Joiners.equal(Lesson::getRoom))
                    // ... and penalize each pair with a hard weight.
                    .penalize("Room conflict", HardSoftScore.ONE_HARD);
        }

        Constraint teacherConflict(ConstraintFactory constraintFactory) {
            // A teacher can teach at most one lesson at the same time.
            return constraintFactory
                    .forEachUniquePair(Lesson.class,
                            Joiners.equal(Lesson::getTimeslot),
                            Joiners.equal(Lesson::getTeacher))
                    .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
        }

        Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
            // A student can attend at most one lesson at the same time.
            return constraintFactory
                    .forEachUniquePair(Lesson.class,
                            Joiners.equal(Lesson::getTimeslot),
                            Joiners.equal(Lesson::getStudentGroup))
                    .penalize("Student group conflict", HardSoftScore.ONE_HARD);
        }

        Constraint teacherRoomStability(ConstraintFactory constraintFactory) {
            // A teacher prefers to teach in a single room.
            return constraintFactory
                    .forEachUniquePair(Lesson.class,
                            Joiners.equal(Lesson::getTeacher))
                    .filter((lesson1, lesson2) -> lesson1.getRoom() != lesson2.getRoom())
                    .penalize("Teacher room stability", HardSoftScore.ONE_SOFT);
        }

        Constraint teacherTimeEfficiency(ConstraintFactory constraintFactory) {
            // A teacher prefers to teach sequential lessons and dislikes gaps between lessons.
            return constraintFactory
                    .forEach(Lesson.class)
                    .join(Lesson.class, Joiners.equal(Lesson::getTeacher),
                            Joiners.equal((lesson) -> lesson.getTimeslot().getDayOfWeek()))
                    .filter((lesson1, lesson2) -> {
                        Duration between = Duration.between(lesson1.getTimeslot().getEndTime(),
                                lesson2.getTimeslot().getStartTime());
                        return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                    })
                    .reward("Teacher time efficiency", HardSoftScore.ONE_SOFT);
        }

        Constraint studentGroupSubjectVariety(ConstraintFactory constraintFactory) {
            // A student group dislikes sequential lessons on the same subject.
            return constraintFactory
                    .forEach(Lesson.class)
                    .join(Lesson.class,
                            Joiners.equal(Lesson::getSubject),
                            Joiners.equal(Lesson::getStudentGroup),
                            Joiners.equal((lesson) -> lesson.getTimeslot().getDayOfWeek()))
                    .filter((lesson1, lesson2) -> {
                        Duration between = Duration.between(lesson1.getTimeslot().getEndTime(),
                                lesson2.getTimeslot().getStartTime());
                        return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) <= 0;
                    })
                    .penalize("Student group subject variety", HardSoftScore.ONE_SOFT);
        }

    }

}
