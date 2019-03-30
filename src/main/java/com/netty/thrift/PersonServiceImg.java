package com.netty.thrift;

import org.apache.thrift.TException;
import thrift.generated.DataException;
import thrift.generated.Person;
import thrift.generated.PersonService;

public class PersonServiceImg implements PersonService.Iface {
    @Override
    public Person getPersonByUsername(String username) throws DataException, TException {
        System.out.println("getPersonByUsername" + username);

        Person person = new Person();
        person.setUsername(username);
        person.setAge(30);
        person.setMarried(true);

        return  person;
    }

    @Override
    public void savePerson(Person person) throws DataException, TException {
        System.out.println("savePerson: ");

        System.out.println(person.getUsername());
        System.out.println(person.getAge());
        System.out.println(person.isMarried());
    }
}
