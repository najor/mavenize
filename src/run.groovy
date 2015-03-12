import groovy.io.FileType

import java.nio.file.Files
import java.nio.file.Path

/**
 * Created by ext-pcrucru on 10/03/2015.
 */

if (args.size() == 0) {
  throw new Exception("You need more parameters")
}

def libPath = args[0];

def repoName = 'my'
def repoLib = libPath + "\\$repoName\\"

def dependencies = '';

def dir = new File(libPath)
dir.eachFileRecurse (FileType.FILES) { file ->
  if (file.isFile() && file.name.endsWith('.jar')) {
    def fileName = file.name
    def matcher = fileName =~ /^(.*)-([\d\.]+)\.jar$/

    print fileName;
    def name
    def version
    if (matcher.matches()) {
      name = matcher.group(1)
      version = matcher.group(2)
    } else {
      name = fileName.replace('.jar', '')
      version = 'unknown'
      fileName = "$name-${version}.jar"
    }

    def subpath = repoLib + name + '\\' + version

    dependencies += createDependency(repoName, name, version)

    print ' *** ' + subpath
    println ''

    new File(subpath).mkdirs()
    file.renameTo(new File(subpath, fileName))
  }
}

def pom = new File(libPath, 'pom.xml');

pom << dependencies
pom << '''
<repository>
    <id>my</id>
    <url>file://${basedir}/lib</url>
</repository>
'''


String createDependency(def groupId, def artifactId, def version) {

  return """
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>$version</version>
</dependency>
"""
}

