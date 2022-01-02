.data
.align 2
    .globl class_nameTab
    .globl Int_protObj
    .globl String_protObj
    .globl bool_const0
    .globl bool_const1
    .globl Main_protObj
    .globl _int_tag
    .globl _string_tag
    .globl _bool_tag
_int_tag:
    .word 2
_string_tag:
    .word 3
_bool_tag:
    .word 4

    .globl heap_start
heap_start:
    .word 0

.text
    .globl Int_init
    .globl String_init
    .globl Bool_init
    .globl Main_init
    .globl Main.main
	li $v0, 10
	syscall		#exit