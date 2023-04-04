package io.lesson04.eventsourcing;

public class MessageCarrier {
    private String command;
    private Person person;

    public MessageCarrier(String command, Person person) {
        this.command = command;
        this.person = person;
    }
    public MessageCarrier() {

    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "MessageCarrier{" +
                "command='" + command + '\'' +
                ", person=" + person +
                '}';
    }
}

