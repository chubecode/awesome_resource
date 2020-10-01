## **How to copy files from multi-folder to another multi-folder?**
For example, you want to copy all resource file name's **ic_main.png** from the drawable folders of **feature** module to **common** module.

Let's do this:
```gradle
task copyAllResource(type: Copy) {
    from "$rootProject.projectDir\\feature\\src\\main\\res"
    include "**/ic_main.png"
    into "$rootProject.projectDir\\common\\src\\main\\res"
}
```
