package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.utils.FilesService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    private final Path nextIdDbPath = File.createTempFile('nextId', '.txt').toPath()
    IdService idService = new IdService(nextIdDbPath, new FilesService())

    def "next id starts from 1 if file was empty"() {
        given:
        idService.getNextIdAndIncrement() == 1

        expect:
        Files.readAllLines(nextIdDbPath) == ['1']

        and:
        idService.getNextIdAndIncrement() == 2
        Files.readAllLines(nextIdDbPath) == ['2']

        and:
        idService.getNextIdAndIncrement() == 3
        Files.readAllLines(nextIdDbPath) == ['3']

        and:
        idService.getNextIdAndIncrement() == 4
        Files.readAllLines(nextIdDbPath) == ['4']
    }

}
