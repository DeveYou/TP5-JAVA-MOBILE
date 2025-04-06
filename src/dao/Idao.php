<?php

interface Idao
{
    function create($o);
    function delete($o);
    function update($o);
    function findAll();
    function findById($id);
}