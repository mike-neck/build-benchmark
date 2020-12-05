import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class RandomWordsGenerateTask extends DefaultTask {

    @Input
    final Property<Integer> size

    @OutputFile
    final RegularFileProperty outputFile

    RandomWordsGenerateTask() {
        this.size = project.objects.property(Integer)
        this.outputFile = project.objects.fileProperty()
    }

    @TaskAction
    void run() {
        int size = this.size.get()
        if (size < 20 || 100_000 < size) {
            throw new InvalidUserDataException("invalid size($size), size should be between 20 and 1000")
        }
        def file = this.outputFile.get().asFile
        def dir = file.parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }
        def url = URI.create("https://random-word-api.herokuapp.com/word?number=$size").toURL()
        def json = new JsonSlurper().parse(url)
        if (!(json instanceof List)) {
            throw new IllegalStateException("expected list but got ${json.class}")
        }
        def jl = json as List<String>
        def list = [] as Set
        jl.eachWithIndex { String entry, int index ->
            list << [index: (index / 2) as int, text: entry]
        }
        def body = list.groupBy { it.index }.collect {
            if (it.value.size() == 2) "${it.value[0].text}${(it.value[1].text as String).capitalize()}"
            else it.value[0].text
        }.collect { "  - $it" }.join('\n')
        def w = new StringWriter()
        w.println('domains:')
        w.println(body)
        file.write(w.toString(), 'UTF-8')
    }
}
